package fit.iuh.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReportRequest {
    @NotNull(message = "Vui lòng chọn đơn hàng cần báo cáo")
    private Long orderId;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Vui lòng nhập chi tiết sự cố")
    private String description;

    // --- MỚI: Thêm trường này ---
    @NotBlank(message = "Vui lòng nhập mã giao dịch (từ tin nhắn ngân hàng/ví)")
    private String transactionCode;
}