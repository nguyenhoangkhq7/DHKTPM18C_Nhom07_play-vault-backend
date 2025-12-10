package fit.iuh.dtos;

import fit.iuh.models.enums.InvoiceStatus;
import fit.iuh.models.enums.PaymentMethod;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class InvoiceTableDto {
    private Long id;
    private String invoiceCode;
    private String customerName;
    private String email;
    private LocalDate issueDate;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private String paymentMethod;

    // --- CONSTRUCTOR NÀY RẤT QUAN TRỌNG ---
    // JPA sẽ gọi cái này khi chạy câu query SELECT new ...
    public InvoiceTableDto(Long id,
                           String customerName,
                           String email,
                           LocalDate issueDate,
                           BigDecimal totalAmount,
                           InvoiceStatus status,
                           PaymentMethod paymentMethodEnum) { // Nhận Enum từ DB
        this.id = id;
        this.invoiceCode = String.format("INV-%05d", id); // Format mã
        this.customerName = customerName;
        this.email = email;
        this.issueDate = issueDate;
        this.totalAmount = totalAmount;
        this.status = status;
        // Xử lý hiển thị phương thức thanh toán
        this.paymentMethod = (paymentMethodEnum != null) ? paymentMethodEnum.toString() : null;
    }
}