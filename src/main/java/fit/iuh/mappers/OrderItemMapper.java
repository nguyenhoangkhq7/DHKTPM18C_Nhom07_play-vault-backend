package fit.iuh.mappers;

import fit.iuh.dtos.OrderItemDTO;
import fit.iuh.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "game.gameBasicInfos.id", target = "gameId")     
    @Mapping(source = "game.gameBasicInfos.name", target = "gameTitle") 
    @Mapping(source = "game.gameBasicInfos.thumbnail", target = "gameThumbnail")  
    @Mapping(source = "promotion.id", target = "promotionId")
    @Mapping(target = "total", ignore = true)  
    OrderItemDTO toDTO(OrderItem item);

    @Mapping(source = "orderId", target = "order.id")
    @Mapping(source = "gameId", target = "game.id")
    @Mapping(source = "promotionId", target = "promotion.id")
    @Mapping(target = "total", ignore = true)
    OrderItem toEntity(OrderItemDTO dto);
}