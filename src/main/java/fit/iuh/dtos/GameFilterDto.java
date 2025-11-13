package fit.iuh.dtos;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * DTO chứa 3 bộ lọc: thể loại, khoảng giá, trạng thái.
 */
@Data
public class GameFilterDto {

    @Positive(message = "Category ID phải lớn hơn 0")
    private Long categoryId;

    @Pattern(
            regexp = "^(\\d+(\\.\\d+)?)-(\\d+(\\.\\d+)?)$",
            message = "priceRange phải có định dạng 'min-max', ví dụ: 10-50"
    )
    private String priceRange;

    @Pattern(
            regexp = "^(ACTIVE|INACTIVE|ALL)?$",
            message = "status chỉ có thể là ACTIVE, INACTIVE hoặc ALL"
    )
    private String status;

    // --- Tiện ích nội bộ: parse min/max từ priceRange ---
    public BigDecimal getMinPrice() {
        if (!StringUtils.hasText(priceRange)) return null;
        try {
            String[] parts = priceRange.split("-");
            return new BigDecimal(parts[0]);
        } catch (Exception e) {
            return null;
        }
    }

    public BigDecimal getMaxPrice() {
        if (!StringUtils.hasText(priceRange)) return null;
        try {
            String[] parts = priceRange.split("-");
            return new BigDecimal(parts[1]);
        } catch (Exception e) {
            return null;
        }
    }
}
