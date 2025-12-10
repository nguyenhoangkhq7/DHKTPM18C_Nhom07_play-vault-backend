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

    // === 1. MAPPING CHO Cart -> CartDto (Cái bạn bị null) ===

    @Mapping(source = "cartItems", target = "items")
    CartDto toDto(Cart cart);



    // ========= PHƯẦN SỬA LỖI ĐÃ ĐƯỢC CẬP NHẬT =========
    /**
     * Phương thức này đã được sửa để truy cập vào entity lồng nhau GameBasicInfos.
     */
    @Mapping(target = "cartItemId", source = "id")
    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "gameName", source = "game.gameBasicInfos.name")
    @Mapping(target = "thumbnail", source = "game.gameBasicInfos.thumbnail")
    @Mapping(target = "originalPrice", source = "price")
    @Mapping(target = "discount", source = "discount")
    @Mapping(target = "finalPrice", expression = "java(calculateFinalPrice(cartItem))")
    CartItemResponse toCartItemResponse(CartItem cartItem);


    @Mapping(target = "cartId", source = "id")
    @Mapping(target = "items", source = "cartItems")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "totalItems", expression = "java(cart.getCartItems() != null ? cart.getCartItems().size() : 0)")
    CartResponse toCartResponse(Cart cart);


    List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems);


    // === 3. HELPER FUNCTION (Dùng chung cho cả 2) ===

    @Named("calculateFinalPrice")
    default BigDecimal calculateFinalPrice(CartItem item) {
        // Đảm bảo code này an toàn với null
        BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
        BigDecimal discount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;

        BigDecimal finalPrice = price.subtract(discount);

        // Đảm bảo giá cuối cùng không bao giờ bị âm
        return finalPrice.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalPrice;
    }

}