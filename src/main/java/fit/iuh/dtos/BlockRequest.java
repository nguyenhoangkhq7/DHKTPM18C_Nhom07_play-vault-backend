package fit.iuh.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record BlockRequest(
        @NotBlank(message = "Lý do khóa không được để trống")
        @Size(min = 10, max = 1000, message = "Lý do từ 10-1000 ký tự")
        String reason
) {}
