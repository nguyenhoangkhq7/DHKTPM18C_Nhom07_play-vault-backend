package fit.iuh.services;

import fit.iuh.dtos.OrderHistoryResponse;
import fit.iuh.models.*;
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<OrderHistoryResponse> getOrderHistory(String username) {
        // 1. Lấy danh sách Order từ DB
        List<Order> orders = orderRepository.findByCustomer_Account_UsernameOrderByCreatedAtDesc(username);

        // 2. Map sang DTO
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private OrderHistoryResponse convertToDTO(Order order) {
        OrderHistoryResponse dto = new OrderHistoryResponse();
        dto.setId(order.getId());
        // Format mã đơn hàng kiểu ORD-001, ORD-015...
        dto.setOrderCode(String.format("ORD-%03d", order.getId()));
        dto.setDate(order.getCreatedAt());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotal());

        // Map danh sách game trong đơn hàng đó
        List<OrderHistoryResponse.PurchasedGameDTO> gameDTOs = new ArrayList<>();

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                Game game = item.getGame();
                GameBasicInfo info = game.getGameBasicInfos();

                OrderHistoryResponse.PurchasedGameDTO gameDTO = new OrderHistoryResponse.PurchasedGameDTO();
                gameDTO.setGameId(game.getId());

                if (info != null) {
                    gameDTO.setGameName(info.getName());
                    gameDTO.setThumbnail(info.getThumbnail());
                    gameDTO.setRequiredAge(info.getRequiredAge());

                    // Lấy tên Publisher
                    if (info.getPublisher() != null) {
                        gameDTO.setPublisherName(info.getPublisher().getStudioName());
                    }

                    // Lấy tên Category
                    if (info.getCategory() != null) {
                        gameDTO.setCategoryName(info.getCategory().getName());
                    }
                }
                gameDTOs.add(gameDTO);
            }
        }
        dto.setGames(gameDTOs);

        return dto;
    }
}