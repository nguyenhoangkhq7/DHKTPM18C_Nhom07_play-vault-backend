package fit.iuh.dtos;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * DTO chứa 3 bộ lọc: thể loại, khoảng giá, trạng thái.
 * Hỗ trợ parse min-max và bỏ qua filter nếu người dùng không nhập.
 */
@Data
public class GameFilterDto {

    @Positive(message = "Category ID phải lớn hơn 0")
    private Long categoryId;

    /**
     * Hỗ trợ định dạng: "min-max"
     * Ví dụ: 10-50, 0-100, 99.9-199.5
     */
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

    // --- Parse min ---
    public BigDecimal getMinPrice() {
        if (!StringUtils.hasText(priceRange)) return null;

        try {
            String[] parts = priceRange.split("-");
            BigDecimal min = new BigDecimal(parts[0]);

            return min;
        } catch (Exception e) {
            return null;
        }
    }

    // --- Parse max ---
    public BigDecimal getMaxPrice() {
        if (!StringUtils.hasText(priceRange)) return null;

        try {
            String[] parts = priceRange.split("-");
            BigDecimal max = new BigDecimal(parts[1]);

            return max;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Kiểm tra nếu người dùng nhập min > max → không hợp lệ
     * Gợi ý cho bạn sử dụng validation thủ công trong service nếu muốn.
     */
    public boolean isPriceRangeValid() {
        BigDecimal min = getMinPrice();
        BigDecimal max = getMaxPrice();
        if (min == null || max == null) return true; // không nhập → hợp lệ

        return min.compareTo(max) <= 0;
    }
}
