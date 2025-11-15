package fit.iuh.mappers;

import fit.iuh.dtos.*;
import fit.iuh.models.Order;
import org.mapstruct.*;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = { OrderItemMapper.class }
)
public interface OrderMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "payment.id", target = "paymentId")
    @Mapping(target = "total", ignore = true)
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

    @Mapping(source = "items", target = "items")
    OrderDto toOrderDto(Order order);

    List<OrderDto> toOrderDtoList(List<Order> orders);

    @Named("formatOrderCode")
    default String formatOrderCode(Long id) {
        if (id == null) return null;
        return String.format("ORD-%03d", id);
    }
}