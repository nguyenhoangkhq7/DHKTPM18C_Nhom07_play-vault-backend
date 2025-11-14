package fit.iuh.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private Long id;
    private Long gameId;
    private String gameTitle;   // chú ý: chỉnh theo getter trong Game (getTitle/getName)
    private String gameThumbnail; // optional
    private Integer quantity;   // nếu bạn có
    private BigDecimal price;
    private BigDecimal total;
}
