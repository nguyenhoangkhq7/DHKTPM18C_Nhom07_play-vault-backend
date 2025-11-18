package fit.iuh.services.impl;

import fit.iuh.dtos.ReviewDto;
import fit.iuh.mappers.ReviewMapper;
import fit.iuh.models.Review;
import fit.iuh.repositories.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements fit.iuh.services.ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    @Override
    public List<ReviewDto> findReviewsByGame_IdOrderByCreatedAtDesc(Long id){
        List<Review> reviews = reviewRepository.findReviewsByGame_IdOrderByCreatedAtDesc(id);
        return reviewMapper.toDtoList(reviews);
    }

}
