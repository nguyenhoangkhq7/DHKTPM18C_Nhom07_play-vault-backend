package fit.iuh.dtos;

import fit.iuh.models.enums.ReportStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AdminReportResponse {
    private Long reportId;
    private Long orderCode;
    private String customerName;
    private String customerEmail;
    private BigDecimal amount;
    private String transactionCode; // Mã giao dịch (lấy từ Payment hoặc user báo cáo)
    private LocalDate createdAt;
    private ReportStatus status;
    private String description;
}