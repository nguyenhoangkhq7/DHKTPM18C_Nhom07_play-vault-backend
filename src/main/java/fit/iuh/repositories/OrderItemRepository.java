package fit.iuh.repositories;

import fit.iuh.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 1. Tính TỔNG doanh thu của Publisher
    @Query("SELECT SUM(oi.price) FROM OrderItem oi " +
            "JOIN oi.game g " +
            "JOIN g.gameBasicInfos info " +
            "WHERE info.publisher.id = :publisherId")
    Double sumTotalRevenueByPublisher(@Param("publisherId") Long publisherId);

    // 2. Tính doanh thu THÁNG NÀY (cần truyền tháng và năm hiện tại)
    @Query("SELECT SUM(oi.price) FROM OrderItem oi " +
            "JOIN oi.game g " +
            "JOIN g.gameBasicInfos info " +
            "JOIN oi.order o " +
            "WHERE info.publisher.id = :publisherId " +
            "AND MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year")
    Double sumMonthlyRevenueByPublisher(@Param("publisherId") Long publisherId,
                                        @Param("month") int month,
                                        @Param("year") int year);

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
    @Query("SELECT MONTH(o.createdAt), SUM(oi.price) FROM OrderItem oi " +
            "JOIN oi.game g " +
            "JOIN g.gameBasicInfos info " +
            "JOIN oi.order o " +
            "WHERE info.publisher.id = :publisherId " +
            "AND YEAR(o.createdAt) = :year " +
            "GROUP BY MONTH(o.createdAt) " +
            "ORDER BY MONTH(o.createdAt)")
    List<Object[]> getRevenueByMonthAndYear(@Param("publisherId") Long publisherId,
                                            @Param("year") int year);
}