package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private Long invoiceId;
    private Long paymentId;
    private BigDecimal newBalance;
    private BigDecimal amount;
}
