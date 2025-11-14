package fit.iuh.mappers;

import fit.iuh.dtos.OrderDTO;
import fit.iuh.models.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = { OrderItemMapper.class }
)
public interface OrderMapper {

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
