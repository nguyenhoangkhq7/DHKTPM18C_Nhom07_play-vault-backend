package fit.iuh.controllers;

import fit.iuh.dtos.PaymentResponse;
import fit.iuh.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

//    @PostMapping("/deposit")
//    public ResponseEntity<?> deposit(@RequestParam BigDecimal amount,
//                                     @RequestParam String method,
//                                     Principal principal) {
//        if (principal == null) {
//            return ResponseEntity.status(401).body("Vui lòng đăng nhập.");
//        }
//
//        String username = principal.getName();
//
//        PaymentResponse response = paymentService.deposit(username, amount, method);
//
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestParam BigDecimal amount,
                                     @RequestParam String method,
                                     Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Vui lòng đăng nhập."));
        }

        try {
            String username = principal.getName();
            PaymentResponse response = paymentService.deposit(username, amount, method);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Nạp tiền thất bại. Vui lòng thử lại sau."));
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập.");
        }

        String username = principal.getName();
        BigDecimal balance = paymentService.getCustomerBalance(username);

        return ResponseEntity.ok(balance);
    }
}
