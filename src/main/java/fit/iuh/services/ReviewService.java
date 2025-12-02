package fit.iuh.services;

import fit.iuh.dtos.ReviewDto;
import fit.iuh.dtos.ReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    List<ReviewDto> findReviewsByGame_IdOrderByCreatedAtDesc(Long id);
    Page<ReviewDto> getReviewsByGameId(Long gameId, Integer rating, Pageable pageable);
    void addReview(ReviewRequest request, String currentUsername);
}
