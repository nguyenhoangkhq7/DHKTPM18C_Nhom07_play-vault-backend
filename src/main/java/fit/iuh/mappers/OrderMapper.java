package fit.iuh.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import fit.iuh.dtos.OrderDto;
import fit.iuh.models.Order;
import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(source = "items", target = "items")
    OrderDto toDto(Order order);

    List<OrderDto> toDtoList(List<Order> orders);
}
