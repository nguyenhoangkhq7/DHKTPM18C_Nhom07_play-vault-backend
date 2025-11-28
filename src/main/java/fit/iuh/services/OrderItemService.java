package fit.iuh.services;

import fit.iuh.dtos.OrderItemDto;
import fit.iuh.mappers.OrderItemMapper;
import fit.iuh.models.OrderItem;
import fit.iuh.repositories.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository itemRepository;
    private final OrderItemMapper itemMapper;

    public List<OrderItemDto> getItemOrderToday() {
        List<OrderItem> items = itemRepository.findAllByOrderItemToday();
        return itemMapper.toDTOList(items);
    }
}