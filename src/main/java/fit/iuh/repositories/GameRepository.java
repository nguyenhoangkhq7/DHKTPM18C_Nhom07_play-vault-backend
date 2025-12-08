package fit.iuh.repositories;

import fit.iuh.dtos.GameSearchResponseDto;
import fit.iuh.dtos.GameWithRatingDto;
import fit.iuh.models.Game;
import fit.iuh.models.enums.SubmissionStatus;
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
    Game findByGameBasicInfos_Id(Long gameBasicInfoId);

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
    @Query("""
    SELECT g
    FROM Game g
    JOIN GameSubmission s ON s.gameBasicInfos = g.gameBasicInfos
    WHERE s.status = :status
""")
    List<Game> findBySubmissionStatus(@Param("status") fit.iuh.models.enums.SubmissionStatus status);

    List<Game> findByGameBasicInfos_Publisher_Id(Long publisherId);

    @Query("""
        SELECT g FROM Game g
        JOIN FETCH g.gameBasicInfos gbi
        JOIN gbi.publisher p
        JOIN p.account a
        WHERE a.username = :username
        ORDER BY g.id
        """)
    List<Game> findByPublisherUsername(@Param("username") String username);
    @Query("SELECT g FROM Game g JOIN FETCH g.gameBasicInfos gbi JOIN FETCH gbi.publisher p JOIN FETCH p.account WHERE g.id IN :ids")
    List<Game> findAllByIdWithPublisher(@Param("ids") List<Long> ids);
    @Query("SELECT oi FROM Order o JOIN o.customer c join c.library oi WHERE o.createdAt = current_date() AND o.status = 'COMPLETED'")  // Tinh chỉnh: uppercase SELECT, thêm () cho current_date
    List<Game> findAllByOrderItemToday();
    // 1. Đếm tổng số Game (Chỉ tính những game có trạng thái APPROVED)
    // Cần JOIN với GameSubmission thông qua GameBasicInfos để check status
    @Query("SELECT COUNT(DISTINCT g) FROM Game g " +
            "JOIN GameSubmission gs ON gs.gameBasicInfos.id = g.gameBasicInfos.id " +
            "WHERE gs.status = fit.iuh.models.enums.SubmissionStatus.APPROVED")
    long countTotalGames();

    // 2. Tính tổng lượt tải (Chỉ tính lượt tải của những game APPROVED)
    // OrderItem -> Game -> GameSubmission -> Check Status
    @Query("SELECT COUNT(oi) FROM OrderItem oi " +
            "JOIN oi.game g " +
            "JOIN GameSubmission gs ON gs.gameBasicInfos.id = g.gameBasicInfos.id " +
            "WHERE gs.status = fit.iuh.models.enums.SubmissionStatus.APPROVED")
    long countTotalDownloads();

    // 3. Tính tổng doanh thu (Chỉ tính doanh thu từ game APPROVED - Tùy chọn nếu cần)
    @Query("SELECT COALESCE(SUM(oi.total), 0) FROM OrderItem oi " +
            "JOIN oi.game g " +
            "JOIN GameSubmission gs ON gs.gameBasicInfos.id = g.gameBasicInfos.id " +
            "WHERE gs.status = fit.iuh.models.enums.SubmissionStatus.APPROVED")
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
            "(SELECT MAX(gs.reviewedAt) FROM GameSubmission gs WHERE gs.gameBasicInfos.id = g.gameBasicInfos.id) ) " +
            "FROM Game g " +
            "LEFT JOIN OrderItem oi ON g.id = oi.game.id " +
            // --- THÊM DÒNG JOIN NÀY ---
            "JOIN GameSubmission gs ON gs.gameBasicInfos.id = g.gameBasicInfos.id " +
            "WHERE (:search IS NULL OR LOWER(g.gameBasicInfos.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:category = 'all' OR g.gameBasicInfos.category.name = :category) " +
            // --- SỬA ĐIỀU KIỆN NÀY: Check gs.status thay vì g.status ---
            "AND gs.status = :status " +
            "GROUP BY g.id, g.gameBasicInfos.name, g.gameBasicInfos.thumbnail, g.gameBasicInfos.publisher.studioName, g.gameBasicInfos.price, g.releaseDate, g.gameBasicInfos.category.name " +
            "ORDER BY SUM(oi.total) DESC")
    Page<GameSearchResponseDto> findGamesSortedByRevenue(@Param("search") String search,
                                                         @Param("category") String category,
                                                         @Param("status") SubmissionStatus status, // Dùng SubmissionStatus
                                                         Pageable pageable);

    // ==========================================================
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
            "(SELECT MAX(gs.reviewedAt) FROM GameSubmission gs WHERE gs.gameBasicInfos.id = g.gameBasicInfos.id) ) " +
            "FROM Game g " +
            "LEFT JOIN OrderItem oi ON g.id = oi.game.id " +
            // --- THÊM DÒNG JOIN NÀY ---
            "JOIN GameSubmission gs ON gs.gameBasicInfos.id = g.gameBasicInfos.id " +
            "WHERE (:search IS NULL OR LOWER(g.gameBasicInfos.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:category = 'all' OR g.gameBasicInfos.category.name = :category) " +
            // --- SỬA ĐIỀU KIỆN NÀY ---
            "AND gs.status = :status " +
            "GROUP BY g.id, g.gameBasicInfos.name, g.gameBasicInfos.thumbnail, g.gameBasicInfos.publisher.studioName, g.gameBasicInfos.price, g.releaseDate, g.gameBasicInfos.category.name " +
            "ORDER BY COUNT(oi) DESC")
    Page<GameSearchResponseDto> findGamesSortedByDownloads(@Param("search") String search,
                                                           @Param("category") String category,
                                                           @Param("status") SubmissionStatus status,
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


    @Query("SELECT oi FROM Order o JOIN o.customer ci join ci.library oi WHERE o.createdAt = current_date() AND o.status = 'COMPLETED'")  // Tinh chỉnh: uppercase SELECT, thêm () cho current_date
    List<Game> findAllByGameToday();


//    // 1) Tìm theo tên (có thể nhập không đầy đủ)
//    @Query("""
//        SELECT g FROM Game g WHERE LOWER(g.gameBasicInfos.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
//    """)
//    List<Game> searchByName(String keyword);

//
//    // 2) Tìm theo cấu hình máy
//    @Query("""
//        SELECT g FROM Game g
//        WHERE
//            (:os IS NULL OR LOWER(g.gameBasicInfos.systemRequirement.os) LIKE LOWER(CONCAT('%', :os, '%')))
//        AND (:cpu IS NULL OR LOWER(g.gameBasicInfos.systemRequirement.cpu) LIKE LOWER(CONCAT('%', :cpu, '%')))
//        AND (:gpu IS NULL OR LOWER(g.gameBasicInfos.systemRequirement.gpu) LIKE LOWER(CONCAT('%', :gpu, '%')))
//        AND (:ram IS NULL OR g.gameBasicInfos.systemRequirement.ram <= :ram)
//        AND (:storage IS NULL OR g.gameBasicInfos.systemRequirement.storage <= :storage)
//    """)
//    List<Game> findBySystem(String os, String cpu, String gpu, Integer ram, Integer storage);


    // 3) Tìm nâng cao
    @Query("""
    SELECT g FROM Game g
    WHERE
        (:os IS NULL OR LOWER(g.gameBasicInfos.systemRequirement.os) LIKE LOWER(CONCAT('%', :os, '%')))
    AND (:cpu IS NULL OR LOWER(g.gameBasicInfos.systemRequirement.cpu) LIKE LOWER(CONCAT('%', :cpu, '%')))
    AND (:gpu IS NULL OR LOWER(g.gameBasicInfos.systemRequirement.gpu) LIKE LOWER(CONCAT('%', :gpu, '%')))
    AND (:ram IS NULL OR g.gameBasicInfos.systemRequirement.ram <= :ram)
    AND (:storage IS NULL OR g.gameBasicInfos.systemRequirement.storage <= :storage)
    AND (:keyword IS NULL OR LOWER(g.gameBasicInfos.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:categoryId IS NULL OR g.gameBasicInfos.category.id = :categoryId)
    AND (
         :minRating IS NULL OR
         (SELECT AVG(r.rating) FROM Review r WHERE r.game.id = g.id) >= :minRating
    )
    AND (:maxPrice IS NULL OR g.gameBasicInfos.price <= :maxPrice)
""")
    List<Game> searchAdvanced(
            String os,
            String cpu,
            String gpu,
            Integer ram,
            Integer storage,
            String keyword,
            Long categoryId,
            Double minRating,
            Double maxPrice
    );
}