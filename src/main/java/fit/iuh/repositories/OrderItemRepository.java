// OrderItemRepository.java – THAY TOÀN BỘ NỘI DUNG BẰNG CÁI NÀY
package fit.iuh.repositories;

import fit.iuh.dtos.GameRevenueDto;
import fit.iuh.dtos.PublisherGameRevenueDto;
import fit.iuh.models.OrderItem;
import fit.iuh.models.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 1. Tính TỔNG doanh thu (Truyền List<OrderStatus>)
    @Query("SELECT SUM(oi.total) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.game g " +
            "JOIN g.gameBasicInfos info " +
            "WHERE info.publisher.id = :publisherId " +
            "AND o.status IN :statuses")
    Double sumTotalRevenueByPublisher(@Param("publisherId") Long publisherId,
                                      @Param("statuses") List<OrderStatus> statuses);

    // 2. Tính doanh thu THÁNG NÀY (Truyền List<OrderStatus>)
    @Query("SELECT SUM(oi.total) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.game g " +
            "JOIN g.gameBasicInfos info " +
            "WHERE info.publisher.id = :publisherId " +
            "AND MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year " +
            "AND o.status IN :statuses")
    Double sumMonthlyRevenueByPublisher(@Param("publisherId") Long publisherId,
                                        @Param("month") int month,
                                        @Param("year") int year,
                                        @Param("statuses") List<OrderStatus> statuses);

    // 3. Tính TỔNG lượt tải (số game đã bán)
    @Query("SELECT COUNT(oi) FROM OrderItem oi " +
            "JOIN oi.game g " +
            "JOIN g.gameBasicInfos info " +
            "WHERE info.publisher.id = :publisherId")
    Long countTotalDownloadsByPublisher(@Param("publisherId") Long publisherId);

    // 4. Tính lượt tải THÁNG NÀY
    @Query("SELECT COUNT(oi) FROM OrderItem oi " +
            "JOIN oi.game g " +
            "JOIN g.gameBasicInfos info " +
            "JOIN oi.order o " +
            "WHERE info.publisher.id = :publisherId " +
            "AND MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year")
    Long countMonthlyDownloadsByPublisher(@Param("publisherId") Long publisherId,
                                          @Param("month") int month,
                                          @Param("year") int year);

    // 5. Query đặc biệt cho BIỂU ĐỒ (Group by Month)
    // Trả về List<Object[]>: [Tháng, Tổng tiền]
    @Query("SELECT MONTH(o.createdAt), SUM(oi.total) FROM OrderItem oi " +
            "JOIN oi.game g " +
            "JOIN g.gameBasicInfos info " +
            "JOIN oi.order o " +
            "WHERE info.publisher.id = :publisherId " +
            "AND YEAR(o.createdAt) = :year " +
            "GROUP BY MONTH(o.createdAt) " +
            "ORDER BY MONTH(o.createdAt)")
    List<Object[]> getRevenueByMonthAndYear(@Param("publisherId") Long publisherId,
                                            @Param("year") int year);

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
        SELECT NEW fit.iuh.dtos.PublisherGameRevenueDto(
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
    List<PublisherGameRevenueDto> findRevenueByGame(
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

    @Query("SELECT oi FROM Order o JOIN o.orderItems oi WHERE o.createdAt = current_date() AND o.status = 'COMPLETED'")  // Tinh chỉnh: uppercase SELECT, thêm () cho current_date
    List<OrderItem> findAllByOrderItemToday();
}