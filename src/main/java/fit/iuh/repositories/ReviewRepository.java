package fit.iuh.repositories;

import fit.iuh.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findReviewsByGame_IdOrderByCreatedAtDesc(Long gameId);
}
