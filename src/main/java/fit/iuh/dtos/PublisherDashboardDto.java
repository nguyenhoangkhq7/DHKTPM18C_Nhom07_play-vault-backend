package fit.iuh.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublisherDashboardDto {
    // Khối "Doanh thu tổng quan"
    private Double totalRevenue;        // Tổng doanh thu (đ92.480.000)
    private Double monthlyRevenue;      // Tháng này (đ15.240.000)

    // Khối "Thống kê nhanh"
    private Long monthlyDownloads;      // Lượt tải tháng này (12.400)
    private Double averageRating;       // Đánh giá TB (4.6)
    private Long totalRatings;          // Lượt đánh giá (1.900)
}