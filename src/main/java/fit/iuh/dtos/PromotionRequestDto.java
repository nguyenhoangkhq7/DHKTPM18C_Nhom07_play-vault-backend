package fit.iuh.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PromotionRequestDto {
    @NotBlank(message = "Tên khuyến mãi không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Ngày bắt đầu là bắt buộc")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc là bắt buộc")
    private LocalDate endDate;

    @NotNull(message = "Trạng thái là bắt buộc")
    private Boolean isActive;

    // Tuân thủ model: cho phép nhập % hoặc số tiền
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "100.0", inclusive = true)
    private BigDecimal discountPercent;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal discountAmount;
}