package fit.iuh.payment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class VNPayController {
    private final VNPayService paymentService;
    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
    }

    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PaymentDTO.VNPayResponse result = paymentService.paymentCallback(request);
        String frontendUrl = "http://localhost:5173/";
        String vnpAmount = request.getParameter("vnp_Amount");
        long finalAmount = 0;
        if (vnpAmount != null) {
            try {
                long amountVal = Long.parseLong(vnpAmount) / 100; // Chia 100 để ra tiền gốc
                long bonus = calculateBonus(amountVal);           // Tính bonus
                finalAmount = amountVal + bonus;                  // Tổng tiền user nhận được
            } catch (NumberFormatException ignored) {
            }
        }
        if (result.code.equals("00")) {
            response.sendRedirect(frontendUrl + "?vnp_ResponseCode=00&vnp_TransactionStatus=00&amount=" + finalAmount);
        } else {
            response.sendRedirect(frontendUrl + "?vnp_ResponseCode=99&vnp_TransactionStatus=02");
        }
    }
    private long calculateBonus(long amount) {
        if (amount == 50000) return 2500;
        if (amount == 100000) return 5000;
        if (amount == 200000) return 15000;
        if (amount == 500000) return 50000;
        return 0; // 10k, 20k không có bonus
    }
}
