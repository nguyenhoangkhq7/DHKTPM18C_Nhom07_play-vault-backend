package fit.iuh.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import fit.iuh.dtos.OrderItemDto;
import fit.iuh.models.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "game.gameBasicInfos.id", target = "gameId")      // hoặc game.id tuỳ bạn muốn
    @Mapping(source = "game.gameBasicInfos.name", target = "gameTitle")
    @Mapping(source = "game.gameBasicInfos.thumbnail", target = "gameThumbnail")
    OrderItemDto toDto(OrderItem item);
}
