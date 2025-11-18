// trong package fit.iuh.dtos
package fit.iuh.dtos;

import fit.iuh.models.Game;
import fit.iuh.models.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor // Thêm constructor cho dễ map
@NoArgsConstructor
public class GameSearchResponseDto {
    private Long id;
    private String name;
    private String thumbnail;
    private BigDecimal price;
    private String categoryName;

    private String shortDescription;
    // -------------------------

    private Double rating;
    private int reviewCount;
    // Tạo một hàm static để chuyển đổi (mapping)
    public static GameSearchResponseDto fromEntity(Game game) {
        GameSearchResponseDto dto = new GameSearchResponseDto();

        // 1. Map ID
        dto.setId(game.getId());

        // 2. Map thông tin cơ bản (Kiểm tra null để tránh lỗi)
        // Lưu ý: Tên hàm getGameBasicInfos (có 's' hay không) phải khớp với file Model Game.java của bạn
        if (game.getGameBasicInfos() != null) {
            dto.setName(game.getGameBasicInfos().getName());
            dto.setThumbnail(game.getGameBasicInfos().getThumbnail());
            dto.setPrice(game.getGameBasicInfos().getPrice());
            dto.setShortDescription(game.getGameBasicInfos().getShortDescription());

            if (game.getGameBasicInfos().getCategory() != null) {
                dto.setCategoryName(game.getGameBasicInfos().getCategory().getName());
            }
        }

        // 3. Map & Tính toán Rating (QUAN TRỌNG)
        List<Review> reviews = game.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            // Đếm số lượng review
            dto.setReviewCount(reviews.size());

            // Tính trung bình cộng số sao
            double average = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            // Làm tròn 1 chữ số thập phân (vd: 4.6)
            dto.setRating(Math.round(average * 10.0) / 10.0);
        } else {
            dto.setReviewCount(0);
            dto.setRating(0.0); // Mặc định 0 sao nếu chưa có review
        }

        return dto;
    }
}