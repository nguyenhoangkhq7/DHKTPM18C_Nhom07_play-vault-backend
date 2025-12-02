package fit.iuh.dtos;

import fit.iuh.models.enums.ReportStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ReportResponse {
    private Long id;
    private String title;
    private String description;

    // Thông tin phản hồi từ Admin
    private String handlerNote;

    private LocalDate createdAt;
    private LocalDate resolvedAt;
    private ReportStatus status;

    // Thông tin đơn hàng liên quan
    private Long orderId;
    private String orderCode; // Ví dụ: ORD-0012
}