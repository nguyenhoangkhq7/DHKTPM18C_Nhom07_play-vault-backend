package fit.iuh.repositories;

import fit.iuh.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findReviewsByGame_IdOrderByCreatedAtDesc(Long gameId);

    // 1. Tính điểm đánh giá trung bình
    @Query("SELECT AVG(r.rating) FROM Review r " +
            "JOIN r.game g " +
            "JOIN g.gameBasicInfos info " +
            "WHERE info.publisher.id = :publisherId")
    Double getAverageRatingByPublisher(@Param("publisherId") Long publisherId);

    // 2. Tính tổng số lượt đánh giá
    @Query("SELECT COUNT(r) FROM Review r " +
            "JOIN r.game g " +
            "JOIN g.gameBasicInfos info " +
            "WHERE info.publisher.id = :publisherId")
    Long countTotalRatingsByPublisher(@Param("publisherId") Long publisherId);
}
