package fit.iuh.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class GameDetailResponseDto {
    private Long id;
    private String name;

    // Giá gốc (luôn có)
    private BigDecimal originalPrice;

    // Giá sau khi áp dụng khuyến mãi (nếu có)
    private BigDecimal discountedPrice;

    // Thông tin khuyến mãi đang áp dụng
    private Boolean isOnSale = false;
    private BigDecimal discountPercent;   // ví dụ: 50
    private BigDecimal discountAmount;    // ví dụ: 100000
    private String promotionName;

    // Các field cũ bạn đang có
    private String description;
    private String shortDescription;
    private String thumbnail;
    private LocalDate releaseDate;
    private String categoryName;
    private String publisherName;
    private Double rating;
    private Long reviewCount;
    private String os;
    private String cpu;
    private String gpu;
    private String ram;
    private String storage;
    private List<ReviewDto> reviewsList; // nếu có
}