package fit.iuh.mappers;

import fit.iuh.dtos.CartDto;
import fit.iuh.models.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GameMapper.class}) // <-- Quan trọng
public interface CartMapper {

    /**
     * Chuyển Cart (Model) sang CartDto (DTO)
     * 'uses = {GameMapper.class}'
     * -> Báo MapStruct tự động dùng GameMapper để xử lý List<CartItem>
     */
    @Mapping(source = "cartItems", target = "items") // Map "cartItems" (Model) -> "items" (DTO)
    CartDto toDto(Cart cart);
}