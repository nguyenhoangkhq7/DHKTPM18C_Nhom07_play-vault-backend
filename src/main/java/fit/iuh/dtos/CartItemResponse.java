package fit.iuh.dtos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long cartItemId;      // ID để xóa item
    private Long gameId;          // ID game để click vào xem chi tiết
    private String gameName;      // Hiển thị tên: "Elden Ring"
    private String thumbnail;     // Hiển thị hình ảnh
    private BigDecimal originalPrice;
    private BigDecimal finalPrice; // Giá sau khi trừ discount (hiển thị lên UI)
}