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

    @Named("formatOrderCode")
    default String formatOrderCode(Long id) {
        if (id == null) return null;
        return String.format("ORD-%03d", id);
    }

    @Mapping(target = "orderCode", expression = "java(\"ORD-\" + String.format(\"%03d\", order.getId()))") // Tạo mã HD001
    @Mapping(source = "customer.fullName", target = "customerName")
    @Mapping(source = "customer.account.email", target = "email")
    @Mapping(target = "gameCount", expression = "java(order.getOrderItems() != null ? order.getOrderItems().size() : 0)")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "status", target = "status")
    OrderTableDto toTableDto(Order order);
}