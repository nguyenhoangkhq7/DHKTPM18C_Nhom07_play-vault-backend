package fit.iuh.payment;

import fit.iuh.models.Customer;
import fit.iuh.models.Invoice;
import fit.iuh.models.Payment;
import fit.iuh.models.enums.InvoiceStatus;
import fit.iuh.models.enums.PaymentMethod;
import fit.iuh.models.enums.PaymentStatus;
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.repositories.InvoiceRepository;
import fit.iuh.repositories.PaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {
   private final VNPAYConfig vnPayConfig;
   private final InvoiceRepository invoiceRepository;
   private final PaymentRepository paymentRepository;
   private final CustomerRepository customerRepository;

   public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
      long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
      String bankCode = request.getParameter("bankCode");

      // Lấy Customer ID từ request (Client phải gửi lên param này)
      String customerId = request.getParameter("customerId");
      if(customerId == null || customerId.isEmpty()) {
         throw new RuntimeException("Customer ID is required");
      }

      Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
      vnpParamsMap.put("vnp_Amount", String.valueOf(amount));

      // --- QUAN TRỌNG: Gửi Customer ID đi kèm OrderInfo ---
      // Format: "Thanh toan don hang|{customerId}"
      vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang|" + customerId);

      // Tạo mã giao dịch unique (Bắt buộc phải có vnp_TxnRef)
      String vnp_TxnRef = VNPayUtil.getRandomNumber(8);
      vnpParamsMap.put("vnp_TxnRef", vnp_TxnRef);

      if (bankCode != null && !bankCode.isEmpty()) {
         vnpParamsMap.put("vnp_BankCode", bankCode);
      }
      vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

      //build query url
      String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
      String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
      String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
      queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
      String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

      return getVnPayResponse("ok", "success", paymentUrl);
   }

   @Transactional // Đảm bảo tính toàn vẹn dữ liệu khi lưu DB
   public PaymentDTO.VNPayResponse paymentCallback(HttpServletRequest request) {
      String vnp_SecureHash = request.getParameter("vnp_SecureHash");
      if (request.getParameter("vnp_ResponseCode").equals("00")) { // Giao dịch thành công ở phía VNPay
         Map<String, String> vnp_Params = new HashMap<>();
         Enumeration<String> enumeration = request.getParameterNames();
         while (enumeration.hasMoreElements()) {
            String paramName = enumeration.nextElement();
            String paramValue = request.getParameter(paramName);
            if ((paramName != null) && (!paramName.isEmpty())) {
               vnp_Params.put(paramName, paramValue);
            }
         }

         vnp_Params.remove("vnp_SecureHash");
         vnp_Params.remove("vnp_SecureHashType");

         String signValue = VNPayUtil.getPaymentURL(vnp_Params, false);
         String checkSum = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), signValue);

         if (checkSum.equals(vnp_SecureHash)) {
            // --- LOGIC LƯU DATABASE ---
            try {
               // 1. Lấy thông tin thanh toán
               long vnpAmount = Long.parseLong(request.getParameter("vnp_Amount")) / 100;
               String vnpOrderInfo = request.getParameter("vnp_OrderInfo");

               // 2. Lấy Customer
               String[] parts = vnpOrderInfo.split("\\|");
               Long customerId = Long.parseLong(parts[1]);
               Customer customer = customerRepository.findById(customerId)
                       .orElseThrow(() -> new RuntimeException("Customer not found"));

               // 3. --- LOGIC CỘNG TIỀN VÀ BONUS ---
               BigDecimal realMoney = BigDecimal.valueOf(vnpAmount);
               BigDecimal bonusMoney = calculateBonus(vnpAmount);
               BigDecimal totalAdd = realMoney.add(bonusMoney);

               // Cập nhật số dư: Balance cũ + Tiền nạp + Bonus
               // Lưu ý: Kiểm tra null nếu balance ban đầu chưa khởi tạo
               BigDecimal currentBalance = customer.getBalance() != null ? customer.getBalance() : BigDecimal.ZERO;
               customer.setBalance(currentBalance.add(totalAdd));

               customerRepository.save(customer);

               // 4. Lưu Invoice (Lưu số tiền thực tế khách trả: realMoney)
               Invoice invoice = new Invoice();
               invoice.setIssueDate(LocalDate.now());
               invoice.setTotalAmount(realMoney); // Hóa đơn ghi nhận 50k
               invoice.setStatus(InvoiceStatus.PAID);
               invoice.setCustomer(customer);
               Invoice savedInvoice = invoiceRepository.save(invoice);

               // 5. Lưu Payment
               Payment payment = new Payment();
               payment.setAmount(realMoney);
               payment.setPaymentDate(LocalDate.now());
               payment.setPaymentMethod(PaymentMethod.VNPAY);
               payment.setStatus(PaymentStatus.SUCCESS);
               payment.setInvoice(savedInvoice);
               paymentRepository.save(payment);

               return getVnPayResponse("00", "Giao dịch thành công", "");

            } catch (Exception e) {
               e.printStackTrace();
               return getVnPayResponse("99", "Lỗi hệ thống: " + e.getMessage(), "");
            }
         } else {
            return getVnPayResponse("97", "Chữ ký không hợp lệ", "");
         }
      } else {
         return getVnPayResponse("99", "Giao dịch thất bại", "");
      }
   }

   private static PaymentDTO.VNPayResponse getVnPayResponse(String number, String statusMessage, String paymentUrl) {
      return PaymentDTO.VNPayResponse.builder()
              .code(number)
              .message(statusMessage)
              .paymentUrl(paymentUrl)
              .build();
   }
   /**
    * Tính toán bonus dựa trên số tiền nạp
    * @param amount Số tiền thực tế khách nạp (VND)
    * @return Số tiền thưởng thêm (Bonus)
    */
   private BigDecimal calculateBonus(long amount) {
      // Dựa trên bảng packages bạn cung cấp
      if (amount == 50000) {
         return BigDecimal.valueOf(2500);
      } else if (amount == 100000) {
         return BigDecimal.valueOf(5000);
      } else if (amount == 200000) {
         return BigDecimal.valueOf(15000);
      } else if (amount == 500000) {
         return BigDecimal.valueOf(50000);
      }
      // Các mốc 10k, 20k hoặc số tiền lạ thì bonus = 0
      return BigDecimal.ZERO;
   }
}