package fit.iuh.dtos;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CartItemDto {
    private Long id;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal finalPrice; // (price - discount)
    private GameBasicInfoDto game; // Thông tin game (đã được map)
}