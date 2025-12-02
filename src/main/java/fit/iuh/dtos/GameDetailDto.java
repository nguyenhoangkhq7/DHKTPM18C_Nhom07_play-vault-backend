package fit.iuh.dtos;

import fit.iuh.models.*;
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
    // --- CÁC TRƯỜNG CŨ (GIỮ NGUYÊN) ---
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String shortDescription;
    private String thumbnail;
    private LocalDate releaseDate;
    private String categoryName;
    private String publisherName;
    private Double rating;
    private int reviewCount;
    private String os;
    private String cpu;
    private String gpu;
    private String ram;
    private String storage;
    private List<ReviewDto> reviewsList = new ArrayList<>();

    // --- CÁC TRƯỜNG THÊM MỚI (CHO FRONTEND CHI TIẾT) ---
    private String title;           // Map title = name
    private String image;           // Frontend mới dùng biến này
    private String coverImage;      // Frontend mới dùng biến này
    private String videoUrl;        // Trailer
    private Integer requireAged;    // Độ tuổi
    private String fileSize;        // Kích thước
    private String status;          // Trạng thái (PENDING/APPROVED)
    private String submittedDate;   // Ngày gửi duyệt
    private Long downloads;         // Lượt tải
    private String developer;       // Nhà phát triển
    private String publisher;       // Frontend mới dùng biến này

    // Các danh sách & Object lồng nhau
    private List<String> category;  // Frontend cần mảng string
    private List<String> platform;  // Danh sách nền tảng
    private List<String> screenshots; // Danh sách ảnh preview
    private SystemRequirementDto minimumRequirements; // Cấu hình máy dạng object

    // =======================================================================
    // INNER DTOs
    // =======================================================================

    // DTO Cấu hình máy (Thêm mới)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SystemRequirementDto {
        private String os;
        private String processor;
        private String memory;
        private String graphics;
        private String storage;
    }

    // DTO Review (Giữ nguyên)
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

    // =======================================================================
    // 1. MAP TỪ GAME (Đã duyệt) - CẬP NHẬT LOGIC
    // =======================================================================
    public static GameDetailDto fromEntity(Game game) {
        GameDetailDto dto = new GameDetailDto();

        // Map các trường cơ bản của Game
        dto.setId(game.getId());
        dto.setReleaseDate(game.getReleaseDate());
        dto.setStatus("APPROVED");
        dto.setSubmittedDate(null);
        dto.setDownloads(0L);

        // Map thông tin từ GameBasicInfo (Dùng hàm helper bên dưới)
        if (game.getGameBasicInfos() != null) {
            mapBasicInfoToDto(dto, game.getGameBasicInfos());
        }

        // Xử lý Review & Rating (Giữ nguyên logic cũ)
        List<Review> reviews = game.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            dto.setReviewCount(reviews.size());
            double avg = reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
            dto.setRating(Math.round(avg * 10.0) / 10.0);
            dto.setReviewsList(reviews.stream().map(ReviewDto::new).collect(Collectors.toList()));
        } else {
            dto.setRating(0.0);
            dto.setReviewCount(0);
        }

        return dto;
    }

    // =======================================================================
    // 2. MAP TỪ GAME_SUBMISSION (Chờ duyệt/Từ chối) - THÊM MỚI
    // =======================================================================
    public static GameDetailDto fromSubmissionEntity(GameSubmission submission) {
        GameDetailDto dto = new GameDetailDto();
        dto.setId(submission.getId());
        dto.setStatus(submission.getStatus() != null ? submission.getStatus().toString() : "UNKNOWN");
        dto.setSubmittedDate(submission.getSubmittedAt() != null ? submission.getSubmittedAt().toString() : "N/A");

        dto.setRating(0.0);
        dto.setReviewCount(0);
        dto.setDownloads(0L);

        if (submission.getGameBasicInfos() != null) {
            mapBasicInfoToDto(dto, submission.getGameBasicInfos());
        }
        return dto;
    }

    // =======================================================================
    // HELPER: MAP BASIC INFO (Tránh lặp code)
    // =======================================================================
    private static void mapBasicInfoToDto(GameDetailDto dto, GameBasicInfo info) {
        // Map các trường cũ
        dto.setName(info.getName());
        dto.setPrice(info.getPrice());
        dto.setDescription(info.getDescription());
        dto.setShortDescription(info.getShortDescription());
        dto.setThumbnail(info.getThumbnail());

        // Map các trường MỚI
        dto.setTitle(info.getName());
        dto.setImage(info.getThumbnail());
        dto.setCoverImage(info.getThumbnail());
        dto.setVideoUrl(info.getTrailerUrl());
        dto.setRequireAged(info.getRequiredAge());

        // Publisher
        if (info.getPublisher() != null) {
            String pName = info.getPublisher().getStudioName();
            dto.setPublisherName(pName); // Cũ
            dto.setPublisher(pName);     // Mới
            dto.setDeveloper(pName);     // Mới
        }

        // Category
        if (info.getCategory() != null) {
            dto.setCategoryName(info.getCategory().getName()); // Cũ
            dto.setCategory(List.of(info.getCategory().getName())); // Mới (List)
        }

        // Platform (Mới)
        if (info.getPlatforms() != null) {
            dto.setPlatform(info.getPlatforms().stream().map(Platform::getName).collect(Collectors.toList()));
        } else {
            dto.setPlatform(List.of("PC"));
        }

        // Screenshots (Mới)
        if (info.getPreviewImages() != null) {
            dto.setScreenshots(info.getPreviewImages().stream().map(PreviewImage::getUrl).collect(Collectors.toList()));
        } else {
            dto.setScreenshots(new ArrayList<>());
        }

        // System Requirements (Cũ & Mới)
        if (info.getSystemRequirement() != null) {
            SystemRequirement sys = info.getSystemRequirement();
            String osStr = sys.getOs() != null ? sys.getOs().toString() : "WINDOW";

            // Set cho trường Cũ (String rời rạc)
            dto.setOs(osStr);
            dto.setCpu(sys.getCpu());
            dto.setGpu(sys.getGpu());
            dto.setRam(sys.getRam());
            dto.setStorage(sys.getStorage());

            // Set cho trường MỚI (Nested Object) và fileSize
            dto.setFileSize(sys.getStorage());
            dto.setMinimumRequirements(new SystemRequirementDto(
                    osStr,
                    sys.getCpu(),
                    sys.getRam(),
                    sys.getGpu(),
                    sys.getStorage()
            ));
        }
    }
}