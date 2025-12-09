package fit.iuh.services;

import fit.iuh.dtos.OrderHistoryResponse;
import fit.iuh.dtos.OrderTableDto;
import fit.iuh.mappers.OrderMapper; // Import Mapper vừa tạo
import fit.iuh.models.Order;
import fit.iuh.models.enums.OrderStatus;
import fit.iuh.repositories.OrderRepository;
import lombok.RequiredArgsConstructor; // Dùng Lombok cho gọn constructor
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // --- PHƯƠNG THỨC MỚI: CHECK QUYỀN SỞ HỮU ---
    @Transactional(readOnly = true)
    public boolean checkIsOwnOrder(Long orderId, String username) {
        return orderRepository.findById(orderId)
                .map(order -> order.getCustomer().getAccount().getUsername().equals(username))
                .orElse(false); // Nếu không tìm thấy order hoặc không khớp user -> trả về false
    }

    public Page<OrderTableDto> getOrdersForAdmin(String keyword, String statusStr, Pageable pageable) {
        OrderStatus status = null;
        if (statusStr != null && !statusStr.isBlank() && !"ALL".equalsIgnoreCase(statusStr)) {
            try {
                status = OrderStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Log lỗi hoặc bỏ qua
            }
        }

        Page<Order> orderPage = orderRepository.findOrdersForAdmin(keyword, status, pageable);

        return orderPage.map(orderMapper::toTableDto);
    }
}