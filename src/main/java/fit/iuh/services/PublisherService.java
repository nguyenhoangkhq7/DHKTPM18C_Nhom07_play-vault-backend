package fit.iuh.services;

import fit.iuh.dtos.*;

import java.util.List;

public interface PublisherService {
    void registerPublisher(PublisherRegisterRequest request);
    // 1. Lấy thống kê tổng quan (Doanh thu, lượt tải...)
    PublisherDashboardDto getPublisherDashboardStats(Long publisherId);

    // 2. Lấy dữ liệu biểu đồ doanh thu
    List<RevenueChartDto> getRevenueChart(Long publisherId, Integer year);

    // 3. Cập nhật thông tin game (Modal cập nhật)
    GameDto updateGameByPublisher(Long publisherId, Long gameId, GameUpdateDto request);

//    void deleteGameByPublisher(Long publisherId, Long gameId);
}