package fit.iuh.repositories;

import fit.iuh.dtos.GameRevenueDto;
import fit.iuh.dtos.RevenueTrendDto;
import fit.iuh.models.Report;
import fit.iuh.models.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // 1. Lấy doanh thu Game
    @Query("SELECT new fit.iuh.dtos.GameRevenueDto(" +
            "g.id, info.name, SUM(oi.total), COUNT(oi.id), info.thumbnail, cat.name) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.game g " +
            "JOIN g.gameBasicInfos info " +
            "LEFT JOIN info.category cat " +
            "WHERE o.status = :status " +
            "AND function('DATE', o.createdAt) >= :from " +
            "AND function('DATE', o.createdAt) <= :to " +
            "GROUP BY g.id, info.name, info.thumbnail, cat.name " +
            "ORDER BY SUM(oi.total) DESC")
    List<GameRevenueDto> getGameRevenueReport(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("status") OrderStatus status
    );

    // 2. Biểu đồ xu hướng
    @Query("SELECT new fit.iuh.dtos.RevenueTrendDto(" +
            "o.createdAt, SUM(o.total), COUNT(o.id)) " +
            "FROM Order o " +
            "WHERE o.status = :status " +
            "AND function('DATE', o.createdAt) >= :from " +
            "AND function('DATE', o.createdAt) <= :to " +
            "GROUP BY o.createdAt " +
            "ORDER BY o.createdAt ASC")
    List<RevenueTrendDto> getRevenueTrend(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("status") OrderStatus status
    );

    // 3. Tổng doanh thu
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o " +
            "WHERE o.status = :status " +
            "AND function('DATE', o.createdAt) >= :from " +
            "AND function('DATE', o.createdAt) <= :to")
    BigDecimal sumTotalRevenue(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("status") OrderStatus status);

    // 4. Tổng đơn hàng
    @Query("SELECT COUNT(o) FROM Order o " +
            "WHERE o.status = :status " +
            "AND function('DATE', o.createdAt) >= :from " +
            "AND function('DATE', o.createdAt) <= :to")
    Long countTotalOrders(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("status") OrderStatus status);

    // 5. Game đã bán
    @Query("SELECT COUNT(oi) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status = :status " +
            "AND function('DATE', o.createdAt) >= :from " +
            "AND function('DATE', o.createdAt) <= :to")
    Long countSoldGames(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("status") OrderStatus status);

    // 6. User mới
    @Query("SELECT COUNT(a) FROM Account a " +
            "WHERE a.role = 'CUSTOMER' " +
            "AND function('DATE', a.createdAt) >= :from " +
            "AND function('DATE', a.createdAt) <= :to")
    Long countNewUsers(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
