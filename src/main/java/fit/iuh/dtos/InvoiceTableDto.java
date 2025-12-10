package fit.iuh.dtos;

import fit.iuh.models.enums.InvoiceStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceTableDto {
    private Long id;
    private String invoiceCode;      // INV-00001
    private String customerName;     // Quan trọng với Admin
    private String email;
    private LocalDate issueDate;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private String paymentMethod;
}