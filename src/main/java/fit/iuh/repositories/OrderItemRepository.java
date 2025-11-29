// OrderItemRepository.java – THAY TOÀN BỘ NỘI DUNG BẰNG CÁI NÀY
package fit.iuh.repositories;

import fit.iuh.dtos.GameRevenueDto;
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
        SELECT NEW fit.iuh.dtos.GameRevenueDto(
            gbi.id,
            gbi.name,
            gbi.thumbnail,
            COUNT(DISTINCT o.id),
            SUM(oi.total)
        )
        FROM OrderItem oi
        JOIN oi.game g
        JOIN g.gameBasicInfos gbi
        JOIN oi.order o
        WHERE gbi.publisher.id = :pubId
          AND o.status = 'COMPLETED'
          AND o.createdAt BETWEEN :from AND :to
        GROUP BY gbi.id, gbi.name, gbi.thumbnail
        ORDER BY SUM(oi.total) DESC
        """)
    List<GameRevenueDto> findRevenueByGame(
            @Param("pubId") Long publisherId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    // 4. Doanh thu theo tháng của 1 game cụ thể
    @Query("SELECT MONTH(o.createdAt), COALESCE(SUM(oi.total), 0) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.game g " +
            "JOIN g.gameBasicInfos gbi " +
            "WHERE gbi.publisher.id = :pubId " +
            "  AND g.id = :gameId " +
            "  AND o.status = 'COMPLETED' " +
            "  AND YEAR(o.createdAt) = :year " +
            "GROUP BY MONTH(o.createdAt) " +
            "ORDER BY MONTH(o.createdAt)")
    List<Object[]> getGameMonthlyRevenueRaw(
            @Param("pubId") Long publisherId,
            @Param("gameId") Long gameId,
            @Param("year") int year);

}



//import java.util.List;
//
//public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

//}