package fit.iuh.services.impl;

import fit.iuh.dtos.ReviewDto;
import fit.iuh.dtos.ReviewRequest;
import fit.iuh.mappers.ReviewMapper;
import fit.iuh.models.Account;
import fit.iuh.models.Customer;
import fit.iuh.models.Game;
import fit.iuh.models.Review;
import fit.iuh.repositories.AccountRepository;
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.repositories.GameRepository;
import fit.iuh.repositories.ReviewRepository;
import fit.iuh.services.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements fit.iuh.services.ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final GameRepository gameRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final JwtService jwtService;

    @Override
    public List<ReviewDto> findReviewsByGame_IdOrderByCreatedAtDesc(Long id){
        List<Review> reviews = reviewRepository.findReviewsByGame_IdOrderByCreatedAtDesc(id);
        return reviewMapper.toDtoList(reviews);
    }

    @Override
    public Page<ReviewDto> getReviewsByGameId(Long gameId, Integer rating, Pageable pageable) {
        Page<Review> reviewPage;

        // Logic lọc
        if (rating != null) {
            reviewPage = reviewRepository.findByGame_IdAndRating(gameId, rating, pageable);
        } else {
            reviewPage = reviewRepository.findByGame_Id(gameId, pageable);
        }
        return reviewPage.map(review -> new ReviewDto(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getCustomer().getFullName()
        ));
    }

    @Override
    public void addReview(ReviewRequest request, String currentUsername) {
        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found"));
        Customer customer = customerRepository.findByAccount_Username(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng cho tài khoản: " + currentUsername));
        game.addReview(customer, request.getRating(), request.getComment());
        gameRepository.save(game);
    }

}
