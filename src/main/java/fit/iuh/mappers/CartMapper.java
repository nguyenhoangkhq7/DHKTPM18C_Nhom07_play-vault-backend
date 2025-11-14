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


    @Mapping(target = "cartItemId", source = "id")
    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "gameName", source = "game.gameBasicInfos.name")
    @Mapping(target = "thumbnail", source = "game.gameBasicInfos.thumbnail")
    @Mapping(target = "originalPrice", source = "price")
    @Mapping(target = "finalPrice", source = ".", qualifiedByName = "calculateFinalPrice")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems);

    @Mapping(target = "cartId", source = "cart.id")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalPrice", source = "calculatedTotal")
    @Mapping(target = "totalItems", expression = "java(items != null ? items.size() : 0)")
    CartResponse toCartResponse(Cart cart, List<CartItemResponse> items, BigDecimal calculatedTotal);


    @Named("calculateFinalPrice")
    default BigDecimal calculateFinalPrice(CartItem item) {
        BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
        BigDecimal discount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;
        return price.subtract(discount);
    }
}