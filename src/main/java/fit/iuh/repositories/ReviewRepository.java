package fit.iuh.repositories;

import fit.iuh.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findReviewsByGame_IdOrderByCreatedAtDesc(Long gameId);
    Page<Review> findByGame_Id(Long gameId, Pageable pageable);
    Page<Review> findByGame_IdAndRating(Long gameId, Integer rating, Pageable pageable);
}
