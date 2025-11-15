package fit.iuh.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDto {

    private Long id;
    private String gameTitle;   // chú ý: chỉnh theo getter trong Game (getTitle/getName)
    private String gameThumbnail; // optional
    private Integer quantity;   // nếu bạn có

    @NotNull(message = "Giá sản phẩm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá sản phẩm phải lớn hơn 0")
    private BigDecimal price;

    private BigDecimal total;

    @NotNull(message = "ID order không được để trống")
    private Long orderId;

    @NotNull(message = "ID game không được để trống")
    private Long gameId;

    private Long promotionId;
}
