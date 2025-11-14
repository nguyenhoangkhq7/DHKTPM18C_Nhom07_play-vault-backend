package fit.iuh.mappers;

import fit.iuh.dtos.OrderItemDTO;
import fit.iuh.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "game.id", target = "gameId")
    @Mapping(source = "promotion.id", target = "promotionId")
    OrderItemDTO toDTO(OrderItem item);

    @Mapping(source = "orderId", target = "order.id")
    @Mapping(source = "gameId", target = "game.id")
    @Mapping(source = "promotionId", target = "promotion.id")
    @Mapping(target = "total", ignore = true) // subtotal sẽ tự tính
    OrderItem toEntity(OrderItemDTO dto);
}
