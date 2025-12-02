// fit.iuh.dtos.ReportResponse.java
package fit.iuh.dtos;

import fit.iuh.models.enums.ReportStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportResponse {
    private Long id;
    private String title;
    private String description;
    private String handlerNote;
    private LocalDate createdAt;
    private LocalDate resolvedAt;
    private ReportStatus status;

    private Long orderId;
    private String orderCode;           // ORD-001

    private Long customerId;
    private String customerName;

    private String handlerUsername;    // username của admin xử lý
}