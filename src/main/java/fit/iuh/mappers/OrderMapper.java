package fit.iuh.mappers;

import fit.iuh.dtos.OrderDTO;
import fit.iuh.models.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import fit.iuh.dtos.OrderHistoryResponse;
import fit.iuh.dtos.PurchasedGameResponse;
import fit.iuh.models.OrderItem;
import org.mapstruct.Named;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = { OrderItemMapper.class }
)
public interface OrderMapper {
// 1. Map từ List<Order> sang List<OrderHistoryResponse>
    List<OrderHistoryResponse> toOrderHistoryResponseList(List<Order> orders);

    // 2. Map từ 1 Order sang OrderHistoryResponse
    @Mapping(target = "orderCode", source = "id", qualifiedByName = "formatOrderCode")
    @Mapping(target = "date", source = "createdAt")
    @Mapping(target = "totalPrice", source = "total")
    @Mapping(target = "games", source = "orderItems") // Map list items sang list games
    OrderHistoryResponse toOrderHistoryResponse(Order order);

    // 3. Map từ OrderItem sang PurchasedGameResponse (MapStruct tự động gọi cái này khi map list ở trên)
    // Do dữ liệu nằm sâu bên trong (game -> gameBasicInfos -> ...), ta dùng dấu chấm để truy cập
    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "gameName", source = "game.gameBasicInfos.name")
    @Mapping(target = "thumbnail", source = "game.gameBasicInfos.thumbnail")
    @Mapping(target = "requiredAge", source = "game.gameBasicInfos.requiredAge")
    @Mapping(target = "publisherName", source = "game.gameBasicInfos.publisher.studioName")
    @Mapping(target = "categoryName", source = "game.gameBasicInfos.category.name")
    PurchasedGameResponse toPurchasedGameResponse(OrderItem orderItem);

    // Hàm hỗ trợ format mã đơn hàng (VD: 1 -> ORD-001)
    @Named("formatOrderCode")
    default String formatOrderCode(Long id) {
        if (id == null) return null;
        return String.format("ORD-%03d", id);
    }
    // ENTITY -> DTO
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "payment.id", target = "paymentId")
    OrderDTO toDTO(Order order);

    // DTO -> ENTITY
    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "paymentId", target = "payment.id")
    // total được tính lại -> không map
    @Mapping(target = "total", ignore = true)
    Order toEntity(OrderDTO dto);
}
