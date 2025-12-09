package fit.iuh.repositories;

import fit.iuh.models.Order;
import fit.iuh.models.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    // fit.iuh.repositories.OrderRepository.java
    List<Order> findByCustomer_Account_UsernameAndStatusOrderByCreatedAtDesc(
            String username, OrderStatus status);
    boolean existsByCustomer_Account_UsernameAndStatusAndOrderItems_Game_Id(
            String username,
            OrderStatus status,
            Long gameId
    );
    // --- API ADMIN: Tìm kiếm và lọc đơn hàng ---
    @EntityGraph(attributePaths = {"customer", "customer.account"})
    @Query("SELECT o FROM Order o " +
            "WHERE (:status IS NULL OR o.status = :status) " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(o.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(o.customer.account.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(o.id AS string) LIKE :keyword) " +
            "ORDER BY o.createdAt DESC")
    Page<Order> findOrdersForAdmin(
            @Param("keyword") String keyword,
            @Param("status") OrderStatus status,
            Pageable pageable
    );
}