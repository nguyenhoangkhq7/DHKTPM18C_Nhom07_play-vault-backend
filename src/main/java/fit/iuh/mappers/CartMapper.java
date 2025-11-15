package fit.iuh.mappers;

import fit.iuh.dtos.CartItemResponse;
import fit.iuh.dtos.CartResponse;
import fit.iuh.dtos.CartDto;
import fit.iuh.dtos.CartItemDto; // <-- THÊM IMPORT NÀY
import fit.iuh.models.Cart;
import fit.iuh.models.CartItem;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = {GameMapper.class}) // Vẫn giữ uses GameMapper
public interface CartMapper {

    @Mapping(source = "cartItems", target = "items")
    CartDto toDto(Cart cart); // Khi map "cartItems", nó sẽ tự tìm toCartItemDtoList ở dưới


    List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems);

    // Nếu bạn muốn tính toán totalItems, bạn sẽ làm như sau (dựa trên List<CartItem> trong Cart entity):
    @Mapping(target = "cartId", source = "id")
    @Mapping(target = "items", source = "cartItems") // MapStruct tự động map List<CartItem> -> List<CartItemResponse>
    @Mapping(target = "totalPrice", source = "totalPrice") // Lấy từ Cart.totalPrice
    @Mapping(target = "totalItems", expression = "java(cart.getCartItems() != null ? cart.getCartItems().size() : 0)")
    CartResponse toCartResponse(Cart cart);


    @Named("calculateFinalPrice")
    default BigDecimal calculateFinalPrice(CartItem item) {
        BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
        BigDecimal discount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;
        return price.subtract(discount);
    }
}