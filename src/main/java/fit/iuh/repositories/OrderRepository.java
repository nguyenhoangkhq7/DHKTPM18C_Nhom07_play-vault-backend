package fit.iuh.repositories;

import fit.iuh.models.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Lấy lịch sử đơn hàng của User.
     * Sử dụng @EntityGraph để EAGER fetch các quan hệ lồng nhau sâu bên trong.
     * Cấu trúc load: Order -> OrderItems -> Game -> GameBasicInfo -> (Publisher, Category)
     */
    @EntityGraph(attributePaths = {
            "orderItems",
            "orderItems.game",
            "orderItems.game.gameBasicInfos",
            "orderItems.game.gameBasicInfos.publisher",
            "orderItems.game.gameBasicInfos.category"
    })
    List<Order> findByCustomer_Account_UsernameOrderByCreatedAtDesc(String username);
}