package fit.iuh.dtos;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GameFilterDto {

    /**
     * Lọc theo tên game (tìm kiếm chứa, không phân biệt hoa thường)
     */
    private String name;
    /**
     * Lọc theo giá sàn
     */
    @DecimalMin(value = "0.0", message = "minPrice phải >= 0", inclusive = true)
    private BigDecimal minPrice;

    /**
     * Lọc theo giá trần
     */
    @DecimalMin(value = "0.0", message = "maxPrice phải >= 0", inclusive = true)
    private BigDecimal maxPrice;


    private String categoryName;
    /**
     * Kiểm tra logic minPrice <= maxPrice
     */
    public boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null) {
            return true; // Bỏ qua nếu 1 trong 2 rỗng
        }
        return minPrice.compareTo(maxPrice) <= 0; // true nếu min <= max
    }
}