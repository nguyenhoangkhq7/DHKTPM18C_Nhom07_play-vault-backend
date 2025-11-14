package fit.iuh.mappers;

import fit.iuh.dtos.CartItemResponse;
import fit.iuh.dtos.CartResponse;
import fit.iuh.models.Cart;
import fit.iuh.models.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    // 1. Map từ CartItem Entity -> CartItemResponse DTO
    @Mapping(target = "cartItemId", source = "id")
    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "gameName", source = "game.gameBasicInfos.name")
    @Mapping(target = "thumbnail", source = "game.gameBasicInfos.thumbnail")
    @Mapping(target = "originalPrice", source = "price")
    @Mapping(target = "finalPrice", source = ".", qualifiedByName = "calculateFinalPrice") // Gọi hàm tính giá
    CartItemResponse toCartItemResponse(CartItem cartItem);

    // 2. Map List (MapStruct tự sinh code vòng lặp dựa trên hàm số 1)
    List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems);

    // 3. Map tổng hợp ra CartResponse (Nhận vào Cart, List Items và Tổng tiền đã tính)
    @Mapping(target = "cartId", source = "cart.id")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalPrice", source = "calculatedTotal")
    @Mapping(target = "totalItems", expression = "java(items != null ? items.size() : 0)")
    CartResponse toCartResponse(Cart cart, List<CartItemResponse> items, BigDecimal calculatedTotal);

    // --- Hàm Helper (Logic tính toán) ---

    @Named("calculateFinalPrice")
    default BigDecimal calculateFinalPrice(CartItem item) {
        BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
        BigDecimal discount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;
        return price.subtract(discount);
    }
}