package fit.iuh.services;

import fit.iuh.dtos.PaymentResponse;
import fit.iuh.models.Customer;
import fit.iuh.models.Payment;
import fit.iuh.models.enums.PaymentMethod;
import fit.iuh.models.enums.PaymentStatus;
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.repositories.InvoiceRepository;
import fit.iuh.repositories.OrderRepository;
import fit.iuh.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final Long DEPOSIT_ORDER_ID = 7L;
    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponse deposit(String username, BigDecimal amount, String method) {
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0");
        }

        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now());

        // ĐOẠN QUAN TRỌNG NHẤT – DÙ FRONTEND GỬI "bank", "momo", "zalopay", "ZALOPAY" gì cũng OK!
        String methodUpper = method.toUpperCase().trim();
        PaymentMethod paymentMethod;
        if ("BANK".equals(methodUpper) || "MOMO".equals(methodUpper)) {
            paymentMethod = PaymentMethod.ZALOPAY; // CỨ ĐỂ LÀ ZALOPAY – vì bạn dùng VietQR → thực chất là chuyển khoản ngân hàng!
        } else {
            paymentMethod = PaymentMethod.valueOf(methodUpper); // chỉ dùng khi gửi đúng ZALOPAY hoặc PAYPAL
        }
        payment.setPaymentMethod(paymentMethod);

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setInvoice(null);

        paymentRepository.save(payment);

        customer.setBalance(customer.getBalance().add(amount));
        customerRepository.save(customer);

        return new PaymentResponse(null, payment.getId(), customer.getBalance(), amount);
    }

    public BigDecimal getCustomerBalance(String username) {
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + username));
        return customer.getBalance();
    }
}
