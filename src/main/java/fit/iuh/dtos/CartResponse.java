package fit.iuh.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    @NotNull(message = "Cart ID cannot be null")
    private Long cartId;

    @NotNull(message = "List of items cannot be null")
    private List<CartItemResponse> items;

    @NotNull(message = "Total price cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total price must be >= 0")
    private BigDecimal totalPrice; // Tổng tiền hiển thị ở "Tóm tắt thanh toán"

    @Min(value = 0, message = "Total items cannot be negative")
    private int totalItems;        // Số lượng sản phẩm
}