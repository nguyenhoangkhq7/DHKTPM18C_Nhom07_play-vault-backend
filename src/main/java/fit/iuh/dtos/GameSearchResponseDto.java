package fit.iuh.dtos;

import fit.iuh.models.Game;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.models.GameSubmission;
import fit.iuh.models.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private Long downloads; // Số lượt tải
    private Double revenue; // Doanh thu thực tế
    private String reviewedAt;
    // =======================================================================
    // PHƯƠNG THỨC TƯƠNG THÍCH NGƯỢC (BACKWARD COMPATIBILITY)
    // Giúp GameDto.java cũ gọi setThumbnail/setCategoryName không bị lỗi
    // =======================================================================

    public GameSearchResponseDto(Long id, String name, String image, String publisher,
                                 BigDecimal price, LocalDate releaseDate, String category,
                                 Long downloads, BigDecimal revenue,LocalDate reviewedAtDate) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.publisher = publisher;
        this.price = price;
        // Chuyển LocalDate sang String ngay trong constructor
        this.releaseDate = releaseDate != null ? releaseDate.toString() : "N/A";
        this.category = category;
        this.downloads = downloads != null ? downloads : 0L;
        this.revenue = revenue != null ? revenue.doubleValue() : 0.0;
        this.reviewedAt = reviewedAtDate != null ? reviewedAtDate.toString() : "N/A";
        // Các trường không select thì set mặc định
        this.status = "APPROVED";
        this.rating = 0.0;
        this.reviewCount = 0;
        this.shortDescription = "";
    }
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
        dto.setStatus("APPROVED"); // Mặc định là Approved vì nằm trong bảng Game

        if (game.getReleaseDate() != null) {
            String dateStr = game.getReleaseDate().toString();
            dto.setReleaseDate(dateStr);
            // Với Game đã duyệt (trong bảng Game), coi ngày phát hành là ngày duyệt
            dto.setReviewedAt(dateStr);
        } else {
            dto.setReleaseDate("Chưa cập nhật");
            dto.setReviewedAt("N/A");
        }

        GameBasicInfo info = game.getGameBasicInfos();
        if (info != null) {
            dto.setName(info.getName());
            dto.setImage(info.getThumbnail());
            dto.setPrice(info.getPrice());
            dto.setShortDescription(info.getShortDescription());

            if (info.getPublisher() != null) {
                dto.setPublisher(info.getPublisher().getStudioName()); // Sửa studioName cho khớp model
            } else {
                dto.setPublisher("Unknown Publisher");
            }

            if (info.getCategory() != null) {
                dto.setCategory(info.getCategory().getName());
            }
        }

        // Tính rating
        List<Review> reviews = game.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            dto.setReviewCount(reviews.size());
            double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            dto.setRating(Math.round(average * 10.0) / 10.0);
        } else {
            dto.setReviewCount(0);
            dto.setRating(0.0);
        }

        // Mặc định 0 nếu không join bảng OrderItem (tránh null)
        dto.setDownloads(0L);
        dto.setRevenue(0.0);

        return dto;
    }

    /**
     * Map từ Submission -> DTO (Dùng cho danh sách Pending)
     */
    public static GameSearchResponseDto fromSubmissionEntity(GameSubmission submission) {
        GameSearchResponseDto dto = new GameSearchResponseDto();
        dto.setId(submission.getId());
        dto.setStatus(submission.getStatus().toString());
        // Use actual submission date or "Chờ duyệt" based on your preference
        dto.setReleaseDate(submission.getSubmittedAt() != null ? submission.getSubmittedAt().toString() : "Chờ duyệt");

        if (submission.getGameBasicInfos() != null) {
            var info = submission.getGameBasicInfos();
            dto.setName(info.getName());
            dto.setImage(info.getThumbnail());
            dto.setPrice(info.getPrice());
            dto.setShortDescription(info.getShortDescription());

            if (info.getCategory() != null) {
                dto.setCategory(info.getCategory().getName());
            }

            // --- FIX: Get actual publisher name instead of hardcoded string ---
            if (info.getPublisher() != null) {
                dto.setPublisher(info.getPublisher().getStudioName()); // Or whatever field stores the name
            } else {
                dto.setPublisher("Unknown Publisher");
            }
            // -----------------------------------------------------------------
        }

        dto.setReviewCount(0);
        dto.setRating(0.0);

        return dto;
    }
}