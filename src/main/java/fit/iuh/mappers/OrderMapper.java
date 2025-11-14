package fit.iuh.mappers;

import fit.iuh.dtos.*;
import fit.iuh.models.Order;
import fit.iuh.models.OrderItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = { OrderItemMapper.class }
)
public interface OrderMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "payment.id", target = "paymentId")
    @Mapping(target = "total", ignore = true) // Tính lại ở Service
    OrderDto toDTO(Order order);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "paymentId", target = "payment.id")
    @Mapping(target = "total", ignore = true)
    Order toEntity(OrderDto dto);

    List<OrderHistoryResponse> toOrderHistoryResponseList(List<Order> orders);

    @Mapping(target = "orderCode", source = "id", qualifiedByName = "formatOrderCode")
    @Mapping(target = "date", source = "createdAt")
    @Mapping(target = "totalPrice", source = "total")
    @Mapping(target = "games", source = "orderItems")
    OrderHistoryResponse toOrderHistoryResponse(Order order);

    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "gameName", source = "game.gameBasicInfos.name")
    @Mapping(target = "thumbnail", source = "game.gameBasicInfos.thumbnail")
    @Mapping(target = "requiredAge", source = "game.gameBasicInfos.requiredAge")
    @Mapping(target = "publisherName", source = "game.gameBasicInfos.publisher.studioName")
    @Mapping(target = "categoryName", source = "game.gameBasicInfos.category.name")
    PurchasedGameResponse toPurchasedGameResponse(OrderItem orderItem);

    @Mapping(source = "items", target = "items")
    OrderDto toOrderDto(Order order);

    List<OrderDto> toOrderDtoList(List<Order> orders);

    @Named("formatOrderCode")
    default String formatOrderCode(Long id) {
        if (id == null) return null;
        return String.format("ORD-%03d", id);
    }
}