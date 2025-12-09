package fit.iuh.dtos;

import fit.iuh.models.enums.InvoiceStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceHistoryDto {
    private Long id;
    private String invoiceCode;      // VD: INV-00123
    private LocalDate issueDate;
    private BigDecimal totalAmount;
    private InvoiceStatus status;    // PAID, PENDING, CANCELLED

    // Thông tin từ bảng Order & Payment
    private String orderCode;        // VD: ORD-00456
    private String paymentMethod;    // VD: ZALOPAY, MOMO

    // Danh sách tên game trong hóa đơn này
    private List<String> gameTitles;
}