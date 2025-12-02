package fit.iuh.repositories;

import fit.iuh.dtos.GameSearchResponseDto;
import fit.iuh.dtos.GameWithRatingDto;
import fit.iuh.models.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>, JpaSpecificationExecutor<Game> {
    List<Game> findByGameBasicInfos_Category_Name(String categoryName);

//    @Query(value = "SELECT g.* " +
//            "FROM games g " +
//            "JOIN reviews r ON g.game_basic_info_id = r.game_id " +
//            "GROUP BY  g.game_basic_info_id" +
//            "HAVING AVG(r.rating) > 0 " +
//            "ORDER BY AVG(r.rating) DESC " +
//            "LIMIT :topN", nativeQuery = true)
@Query(value =
        "SELECT g.*, AVG(r.rating) AS avg_rating " +
                "FROM games g " +
                "JOIN reviews r ON g.game_basic_info_id = r.game_id " +
                "GROUP BY g.id " +
                "HAVING AVG(r.rating) > 0 " +
                "ORDER BY avg_rating DESC " +
                "LIMIT :topN",
        nativeQuery = true)
    List<Game> findTopRatedGames(@Param("topN") int topN);
    List<Game> findByGameBasicInfos_Publisher_Id(Long publisherId);

    // 1. Đếm tổng số Game
    // Vì 'Game' được sinh ra từ 'GameSubmission' đã duyệt, nên đếm tất cả Game là được.
    @Query("SELECT COUNT(g) FROM Game g")
    long countTotalGames();

    // 2. Tính tổng lượt tải
    // Dựa trên Class Diagram: Không có trường 'downloads' trong Game.
    // Lượt tải = Số lượng OrderItem đã bán ra (Mỗi OrderItem tương ứng 1 game được mua/tải)
    // Lưu ý: Query trực tiếp entity OrderItem (Dù đang ở trong GameRepository vẫn query được nếu cùng context)
    @Query("SELECT COUNT(oi) FROM OrderItem oi")
    long countTotalDownloads();

    // 3. Tính tổng doanh thu
    // Doanh thu = Tổng giá trị (price) của tất cả OrderItem
    // Sử dụng COALESCE để trả về 0 nếu chưa có đơn hàng nào (tránh lỗi null)
    @Query("SELECT COALESCE(SUM(oi.total), 0) FROM OrderItem oi")
    double sumTotalRevenue();

    @Query("SELECT new fit.iuh.dtos.GameSearchResponseDto(" +
            "g.id, " +
            "g.gameBasicInfos.name, " +
            "g.gameBasicInfos.thumbnail, " +
            "g.gameBasicInfos.publisher.studioName, " +
            "cast(g.gameBasicInfos.price as BigDecimal), " +
            "g.releaseDate, " +
            "g.gameBasicInfos.category.name, " +
            "COUNT(oi), " +
            "cast(COALESCE(SUM(oi.total), 0) as BigDecimal), " +
            // Subquery lấy ngày duyệt từ GameSubmission
            "(SELECT MAX(gs.reviewedAt) FROM GameSubmission gs WHERE gs.gameBasicInfos.id = g.gameBasicInfos.id) ) " +
            "FROM Game g " +
            "LEFT JOIN OrderItem oi ON g.id = oi.game.id " +
            "WHERE (:search IS NULL OR LOWER(g.gameBasicInfos.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:category = 'all' OR g.gameBasicInfos.category.name = :category) " +
            "GROUP BY g.id, g.gameBasicInfos.name, g.gameBasicInfos.thumbnail, g.gameBasicInfos.publisher.studioName, g.gameBasicInfos.price, g.releaseDate, g.gameBasicInfos.category.name " +
            "ORDER BY SUM(oi.total) DESC")
    Page<GameSearchResponseDto> findGamesSortedByRevenue(@Param("search") String search,
                                                         @Param("category") String category,
                                                         Pageable pageable);

    // 2. Sắp xếp theo Lượt tải (Downloads)
    @Query("SELECT new fit.iuh.dtos.GameSearchResponseDto(" +
            "g.id, " +
            "g.gameBasicInfos.name, " +
            "g.gameBasicInfos.thumbnail, " +
            "g.gameBasicInfos.publisher.studioName, " +
            "cast(g.gameBasicInfos.price as BigDecimal), " +
            "g.releaseDate, " +
            "g.gameBasicInfos.category.name, " +
            "COUNT(oi), " +
            "cast(COALESCE(SUM(oi.total), 0) as BigDecimal), " +
            // Subquery lấy ngày duyệt
            "(SELECT MAX(gs.reviewedAt) FROM GameSubmission gs WHERE gs.gameBasicInfos.id = g.gameBasicInfos.id) ) " +
            "FROM Game g " +
            "LEFT JOIN OrderItem oi ON g.id = oi.game.id " +
            "WHERE (:search IS NULL OR LOWER(g.gameBasicInfos.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:category = 'all' OR g.gameBasicInfos.category.name = :category) " +
            "GROUP BY g.id, g.gameBasicInfos.name, g.gameBasicInfos.thumbnail, g.gameBasicInfos.publisher.studioName, g.gameBasicInfos.price, g.releaseDate, g.gameBasicInfos.category.name " +
            "ORDER BY COUNT(oi) DESC")
    Page<GameSearchResponseDto> findGamesSortedByDownloads(@Param("search") String search,
                                                           @Param("category") String category,
                                                           Pageable pageable);

    // ... các query cũ giữ nguyên ...

    // 3. Query Mặc định (Load trang lần đầu): Lấy kèm Stats nhưng Order by ID giảm dần
    // ... các query cũ giữ nguyên ...

    // THÊM MỚI: Hàm này lấy thống kê cho một danh sách ID cụ thể
    // Trả về: Object[] gồm {gameId, lượt_tải, doanh_thu}
    @Query("SELECT oi.game.id, COUNT(oi), COALESCE(SUM(oi.total), 0) " +
            "FROM OrderItem oi " +
            "WHERE oi.game.id IN :ids " +
            "GROUP BY oi.game.id")
    List<Object[]> findStatsForGameIds(@Param("ids") List<Long> ids);
}