package fit.iuh.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    private Long cartId;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice; // Tổng tiền hiển thị ở "Tóm tắt thanh toán"
    private int totalItems;        // Số lượng sản phẩm
}