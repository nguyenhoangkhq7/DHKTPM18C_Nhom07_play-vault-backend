package fit.iuh.repositories;

import fit.iuh.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ========================================================================
    // 1. PHÂN TRANG ĐƠN HÀNG THEO CUSTOMER (Admin / quangvinh)
    // ========================================================================
    Page<Order> findByCustomer_Id(Long customerId, Pageable pageable);


    // ========================================================================
    // 2. LỊCH SỬ MUA HÀNG CỦA USER (main) - Tối ưu bằng EntityGraph
    // ========================================================================
    @EntityGraph(attributePaths = {
            "orderItems",
            "orderItems.game",
            "orderItems.game.gameBasicInfos",
            "orderItems.game.gameBasicInfos.publisher",
            "orderItems.game.gameBasicInfos.category"
    })
    List<Order> findByCustomer_Account_UsernameOrderByCreatedAtDesc(String username);
}