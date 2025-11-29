package fit.iuh.dtos;

import fit.iuh.models.Game;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.models.GameSubmission;
import fit.iuh.models.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameSearchResponseDto {
    private Long id;
    private String name;
    private String image;       // Frontend mới dùng 'image'
    private String publisher;
    private BigDecimal price;
    private String releaseDate;
    private String status;
    private String category;    // Frontend mới dùng 'category'
    private String shortDescription;
    private Double rating;
    private int reviewCount;

    // =======================================================================
    // PHƯƠNG THỨC TƯƠNG THÍCH NGƯỢC (BACKWARD COMPATIBILITY)
    // Giúp GameDto.java cũ gọi setThumbnail/setCategoryName không bị lỗi
    // =======================================================================

    // Khi GameDto gọi setThumbnail, ta chuyển nó vào biến image
    public void setThumbnail(String thumbnail) {
        this.image = thumbnail;
    }

    // Nếu cần get
    public String getThumbnail() {
        return this.image;
    }

    // Khi GameDto gọi setCategoryName, ta chuyển nó vào biến category
    public void setCategoryName(String categoryName) {
        this.category = categoryName;
    }

    public String getCategoryName() {
        return this.category;
    }
    // =======================================================================

    /**
     * Map từ Game Entity -> DTO (Dùng cho danh sách Approved)
     */
    public static GameSearchResponseDto fromEntity(Game game) {
        GameSearchResponseDto dto = new GameSearchResponseDto();
        dto.setId(game.getId());


        // Sửa logic lấy releaseDate từ Game (như yêu cầu trước đó)
        if (game.getReleaseDate() != null) {
            dto.setReleaseDate(game.getReleaseDate().toString());
        } else {
            dto.setReleaseDate("Chưa cập nhật");
        }

        // Sửa logic lấy info từ OneToOne
        GameBasicInfo info = game.getGameBasicInfos();

        if (info != null) {
            dto.setName(info.getName());
            dto.setImage(info.getThumbnail()); // set vào image
            dto.setPrice(info.getPrice());
            dto.setShortDescription(info.getShortDescription());

            if (info.getPublisher() != null) {
                dto.setPublisher(info.getPublisher().getStudioName());
            } else {
                dto.setPublisher("Unknown Publisher");
            }

            if (info.getCategory() != null) {
                dto.setCategory(info.getCategory().getName()); // set vào category
            } else {
                dto.setCategory("Uncategorized");
            }
        } else {
            dto.setName("Game ID: " + game.getId());
            dto.setPublisher("N/A");
            dto.setPrice(BigDecimal.ZERO);
        }

        List<Review> reviews = game.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            dto.setReviewCount(reviews.size());
            double average = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            dto.setRating(Math.round(average * 10.0) / 10.0);
        } else {
            dto.setReviewCount(0);
            dto.setRating(0.0);
        }

        return dto;
    }

    /**
     * Map từ Submission -> DTO (Dùng cho danh sách Pending)
     */
    public static GameSearchResponseDto fromSubmissionEntity(GameSubmission submission) {
        GameSearchResponseDto dto = new GameSearchResponseDto();
        dto.setId(submission.getId());
        dto.setStatus(submission.getStatus().toString());
        dto.setReleaseDate("Chờ duyệt");

        if (submission.getGameBasicInfos() != null) {
            dto.setName(submission.getGameBasicInfos().getName());
            dto.setImage(submission.getGameBasicInfos().getThumbnail());
            dto.setPrice(submission.getGameBasicInfos().getPrice());
            dto.setShortDescription(submission.getGameBasicInfos().getShortDescription());

            if (submission.getGameBasicInfos().getCategory() != null) {
                dto.setCategory(submission.getGameBasicInfos().getCategory().getName());
            }
            dto.setPublisher("Pending Publisher");
        }

        dto.setReviewCount(0);
        dto.setRating(0.0);

        return dto;
    }
}