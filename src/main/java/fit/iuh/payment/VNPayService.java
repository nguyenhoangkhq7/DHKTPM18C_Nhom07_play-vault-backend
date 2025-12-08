package fit.iuh.payment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {
   private final VNPAYConfig vnPayConfig;
   public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
      long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
      String bankCode = request.getParameter("bankCode");
      Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
      vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
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
   public PaymentDTO.VNPayResponse paymentCallback(HttpServletRequest request) {
      String vnp_SecureHash = request.getParameter("vnp_SecureHash");
      if (request.getParameter("vnp_ResponseCode").equals("00")) {
         // Lấy toàn bộ tham số
         Map<String, String> vnp_Params = new HashMap<>();
         Enumeration<String> enumeration = request.getParameterNames();
         while (enumeration.hasMoreElements()) {
            String paramName = enumeration.nextElement();
            String paramValue = request.getParameter(paramName);
            if ((paramName != null) && (!paramName.isEmpty())) {
               vnp_Params.put(paramName, paramValue);
            }
         }

         // Xóa 2 tham số về hash khỏi mảng để tính lại checksum
         vnp_Params.remove("vnp_SecureHash");
         vnp_Params.remove("vnp_SecureHashType");

         // Tính lại checksum dựa trên key của bạn
         String signValue = VNPayUtil.getPaymentURL(vnp_Params, false);
         String checkSum = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), signValue);

         // So sánh
         // Hash khớp -> Giao dịch hợp lệ
         if (checkSum.equals(vnp_SecureHash)) {
            // TODO: Tại đây bạn nên cập nhật trạng thái đơn hàng trong Database

            return getVnPayResponse("00", "Giao dịch thành công", "");
         } else { // Hash không khớp -> Có dấu hiệu giả mạo
            return getVnPayResponse("97", "Chữ ký không hợp lệ", "");
         }
      } else {
         // Giao dịch thất bại (Khách hủy, hết tiền...)
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
}