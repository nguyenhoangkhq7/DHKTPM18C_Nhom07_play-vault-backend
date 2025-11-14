package fit.iuh.mappers;

import fit.iuh.dtos.OrderItemDTO;
import fit.iuh.dtos.PurchasedGameResponse;
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

    // 3. Map từ OrderItem sang PurchasedGameResponse (MapStruct tự động gọi cái này khi map list ở trên)
    // Do dữ liệu nằm sâu bên trong (game -> gameBasicInfos -> ...), ta dùng dấu chấm để truy cập
    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "gameName", source = "game.gameBasicInfos.name")
    @Mapping(target = "thumbnail", source = "game.gameBasicInfos.thumbnail")
    @Mapping(target = "requiredAge", source = "game.gameBasicInfos.requiredAge")
    @Mapping(target = "publisherName", source = "game.gameBasicInfos.publisher.studioName")
    @Mapping(target = "categoryName", source = "game.gameBasicInfos.category.name")
    PurchasedGameResponse toPurchasedGameResponse(OrderItem orderItem);
}
