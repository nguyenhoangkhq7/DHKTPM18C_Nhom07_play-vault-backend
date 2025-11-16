package fit.iuh.mappers;

import fit.iuh.dtos.CartItemResponse;
import fit.iuh.dtos.CartResponse;
import fit.iuh.dtos.CartDto;
import fit.iuh.models.Cart;
import fit.iuh.models.CartItem;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = {GameMapper.class})
public interface CartMapper {

    @Mapping(source = "cartItems", target = "items")
    CartDto toDto(Cart cart);

    List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems);


    // ========= PHƯẦN SỬA LỖI ĐÃ ĐƯỢC CẬP NHẬT =========
    /**
     * Phương thức này đã được sửa để truy cập vào entity lồng nhau GameBasicInfos.
     */
    @Mapping(target = "cartItemId", source = "id")
    @Mapping(target = "gameId", source = "game.id")

    // SỬA LỖI: Truy cập qua GameBasicInfos
    @Mapping(target = "gameName", source = "game.gameBasicInfos.name")
    @Mapping(target = "thumbnail", source = "game.gameBasicInfos.thumbnail")

    @Mapping(target = "originalPrice", source = "price")
    @Mapping(target = "finalPrice", source = "item", qualifiedByName = "calculateFinalPrice")
    CartItemResponse toCartItemResponse(CartItem item);
    // ========= KẾT THÚC PHẦN SỬA LỖI =========


    @Mapping(target = "cartId", source = "id")
    @Mapping(target = "items", source = "cartItems")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "totalItems", expression = "java(cart.getCartItems() != null ? cart.getCartItems().size() : 0)")
    CartResponse toCartResponse(Cart cart);


    @Named("calculateFinalPrice")
    default BigDecimal calculateFinalPrice(CartItem item) {
        BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
        BigDecimal discount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;
        return price.subtract(discount);
    }
}