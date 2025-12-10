package fit.iuh.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemResponse {
    @NotNull(message = "Cart Item ID cannot be null")
    private Long cartItemId;      // ID để xóa item

    @NotNull(message = "Game ID cannot be null")
    private Long gameId;

    @NotBlank(message = "Game name cannot be empty")
    private String gameName;

    private String thumbnail;

    @NotNull(message = "Original price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Original price must be >= 0")
    private BigDecimal originalPrice;
    private BigDecimal discount;

    @NotNull(message = "Final price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Final price must be >= 0")
    private BigDecimal finalPrice; // Giá sau khi trừ discount
}