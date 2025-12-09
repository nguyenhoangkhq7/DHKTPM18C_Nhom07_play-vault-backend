package fit.iuh.payment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/payment")
@RequiredArgsConstructor
public class VNPayController {
    private final VNPayService paymentService;
    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
    }

    @GetMapping("/vn-pay-callback")
    public ResponseObject<PaymentDTO.VNPayResponse> payCallbackHandler(HttpServletRequest request) {
        PaymentDTO.VNPayResponse response = paymentService.paymentCallback(request);
        if (response.code.equals("00")) {
            return new ResponseObject<>(HttpStatus.OK, "Success", response);
        } else {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST, "Failed", response);
        }
    }
}
