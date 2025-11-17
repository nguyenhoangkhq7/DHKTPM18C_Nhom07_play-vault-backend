package fit.iuh.dtos;

import fit.iuh.models.Game;
import fit.iuh.models.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDetailDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String shortDescription;
    private String thumbnail;
    private LocalDate releaseDate;

    // Thông tin phụ
    private String categoryName;
    private String publisherName;

    // Rating
    private Double rating;
    private int reviewCount;

    // Cấu hình máy (System Requirements)
    private String os;
    private String cpu;
    private String gpu;
    private String ram;
    private String storage;

    // Danh sách review chi tiết (để hiển thị ở Tab Reviews)
    private List<ReviewDto> reviewsList = new ArrayList<>();

    // Inner DTO cho Review
    @Data
    public static class ReviewDto {
        private String authorName;
        private int rating;
        private String comment;
        private String date;

        public ReviewDto(Review r) {
            this.authorName = r.getCustomer() != null ? r.getCustomer().getFullName() : "Ẩn danh";
            this.rating = r.getRating();
            this.comment = r.getComment();
            this.date = r.getCreatedAt() != null ? r.getCreatedAt().toString() : "";
        }
    }

    public static GameDetailDto fromEntity(Game game) {
        GameDetailDto dto = new GameDetailDto();
        dto.setId(game.getId());
        dto.setReleaseDate(game.getReleaseDate());

        if (game.getGameBasicInfos() != null) {
            var info = game.getGameBasicInfos();
            dto.setName(info.getName());
            dto.setPrice(info.getPrice());
            dto.setDescription(info.getDescription());
            dto.setShortDescription(info.getShortDescription());
            dto.setThumbnail(info.getThumbnail());

            if (info.getCategory() != null) dto.setCategoryName(info.getCategory().getName());
            if (info.getPublisher() != null) dto.setPublisherName(info.getPublisher().getStudioName());

            if (info.getSystemRequirement() != null) {
                dto.setOs(String.valueOf(info.getSystemRequirement().getOs()));
                dto.setCpu(info.getSystemRequirement().getCpu());
                dto.setGpu(info.getSystemRequirement().getGpu());
                dto.setRam(info.getSystemRequirement().getRam());
                dto.setStorage(info.getSystemRequirement().getStorage());
            }
        }

        // Xử lý Review & Rating
        List<Review> reviews = game.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            dto.setReviewCount(reviews.size());
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            dto.setRating(Math.round(avg * 10.0) / 10.0);

            // Map danh sách review sang DTO
            dto.setReviewsList(reviews.stream().map(ReviewDto::new).collect(Collectors.toList()));
        } else {
            dto.setRating(0.0);
            dto.setReviewCount(0);
        }

        return dto;
    }
}