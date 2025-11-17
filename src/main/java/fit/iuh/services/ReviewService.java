package fit.iuh.services;

import fit.iuh.dtos.ReviewDto;

import java.util.List;

public interface ReviewService {
    List<ReviewDto> findReviewsByGame_IdOrderByCreatedAtDesc(Long id);
}
