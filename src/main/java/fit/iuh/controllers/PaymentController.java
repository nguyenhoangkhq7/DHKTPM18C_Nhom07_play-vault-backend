package fit.iuh.controllers;

import fit.iuh.models.Customer;
import fit.iuh.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // Endpoint nạp tiền
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestParam BigDecimal amount, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập.");
        }

        String username = principal.getName();
        Customer updatedCustomer = paymentService.deposit(username, amount);

        return ResponseEntity.ok("Nạp thành công! Số dư mới: " + updatedCustomer.getBalance() + " GCoin");
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập.");
        }

        String username = principal.getName();

        Customer customer = paymentService.getCustomerByUsername(username);

        if (customer == null) {
            return ResponseEntity.status(404).body("Không tìm thấy khách hàng.");
        }

        return ResponseEntity.ok(customer.getBalance());
    }

}