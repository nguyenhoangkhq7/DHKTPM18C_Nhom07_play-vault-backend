// fit.iuh.dtos.ReportRequest.java
package fit.iuh.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReportRequest {

    @NotBlank(message = "Tiêu đề lỗi không được để trống")
    private String title;

    @NotBlank(message = "Mô tả chi tiết không được để trống")
    private String description;

    @NotNull(message = "Vui lòng chọn đơn hàng bị lỗi")
    private Long orderId;                            // BẮT BUỘC theo ERD

    private List<MultipartFile> attachments;        // Ảnh/video minh họa (tùy chọn)
}