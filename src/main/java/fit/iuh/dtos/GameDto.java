package fit.iuh.dtos;

import fit.iuh.models.Game;
import fit.iuh.models.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDto {
    private Long id;
    private LocalDate releaseDate;
    private GameBasicInfoDto gameBasicInfos;
    private String categoryName;

    private Double rating;      // Điểm trung bình (VD: 4.5)
    private int reviewCount;    // Số lượng đánh giá (VD: 10)

    public static GameSearchResponseDto fromEntity(Game game) {
        GameSearchResponseDto dto = new GameSearchResponseDto();

        // Map thông tin cơ bản
        dto.setId(game.getId());
        if (game.getGameBasicInfos() != null) {
            dto.setName(game.getGameBasicInfos().getName());
            dto.setPrice(game.getGameBasicInfos().getPrice());
            dto.setThumbnail(game.getGameBasicInfos().getThumbnail());
            dto.setShortDescription(game.getGameBasicInfos().getShortDescription());

            if (game.getGameBasicInfos().getCategory() != null) {
                dto.setCategoryName(game.getGameBasicInfos().getCategory().getName());
            }
        }

        // --- LOGIC TÍNH ĐIỂM RATING ---
        List<Review> reviews = game.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            // Tính số lượng review
            dto.setReviewCount(reviews.size());

            // Tính trung bình cộng (Average)
            double average = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            // Làm tròn 1 chữ số thập phân (VD: 4.666 -> 4.7)
            dto.setRating(Math.round(average * 10.0) / 10.0);
        } else {
            dto.setReviewCount(0);
            dto.setRating(0.0);
        }
        // -----------------------------

        return dto;
    }
}

