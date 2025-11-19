// OrderItemRepository.java – THAY TOÀN BỘ NỘI DUNG BẰNG CÁI NÀY
package fit.iuh.repositories;

import fit.iuh.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 1. Tổng doanh thu Publisher
    @Query("SELECT COALESCE(SUM(oi.total), 0) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.game g " +
            "JOIN g.gameBasicInfos gbi " +
            "WHERE gbi.publisher.id = :pubId " +
            "  AND o.status = 'COMPLETED' " +
            "  AND o.createdAt BETWEEN :from AND :to")
    BigDecimal getRevenueByPublisher(
            @Param("pubId") Long publisherId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    // 2. Doanh thu theo tháng
    @Query("SELECT MONTH(o.createdAt), COALESCE(SUM(oi.total), 0) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.game g " +
            "JOIN g.gameBasicInfos gbi " +
            "WHERE gbi.publisher.id = :pubId " +
            "  AND o.status = 'COMPLETED' " +
            "  AND YEAR(o.createdAt) = :year " +
            "GROUP BY MONTH(o.createdAt) " +
            "ORDER BY MONTH(o.createdAt)")
    List<Object[]> getMonthlyRevenueRaw(
            @Param("pubId") Long publisherId,
            @Param("year") int year);

    // 3. Doanh thu theo game (gộp theo game, trả về OrderItem để mapper xử lý)
    @Query("""

            SELECT oi FROM OrderItem oi
           JOIN oi.game g
           JOIN g.gameBasicInfos gbi
           JOIN oi.order o
           WHERE gbi.publisher.id = :pubId
             AND o.status = 'COMPLETED'
             AND o.createdAt BETWEEN :from AND :to
           ORDER BY oi.total DESC
           """)
    List<OrderItem> findRevenueByGame(
            @Param("pubId") Long publisherId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("SELECT oi FROM Order o JOIN o.orderItems oi WHERE o.createdAt = current_date() AND o.status = 'COMPLETED'")  // Tinh chỉnh: uppercase SELECT, thêm () cho current_date
    List<OrderItem> findAllByOrderItemToday();
}