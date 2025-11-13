package fit.iuh.services;

import fit.iuh.dtos.OrderHistoryResponse;
import fit.iuh.mappers.OrderMapper; // Import Mapper vừa tạo
import fit.iuh.models.Order;
import fit.iuh.repositories.OrderRepository;
import lombok.RequiredArgsConstructor; // Dùng Lombok cho gọn constructor
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper; // Inject Mapper

    @Transactional(readOnly = true)
    public List<OrderHistoryResponse> getOrderHistory(String username) {
        // 1. Lấy danh sách Order từ DB
        List<Order> orders = orderRepository.findByCustomer_Account_UsernameOrderByCreatedAtDesc(username);

        // 2. Dùng MapStruct để chuyển đổi (1 dòng duy nhất!)
        return orderMapper.toOrderHistoryResponseList(orders);
    }
}