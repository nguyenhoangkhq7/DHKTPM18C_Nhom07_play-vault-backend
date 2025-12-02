package fit.iuh.services;

import fit.iuh.dtos.*;
import java.util.List;

public interface PublisherService {
    List<PublisherDto> findAll();
    void registerPublisher(PublisherRegisterRequest request);
    PublisherDashboardDto getPublisherDashboardStats(Long publisherId);
    List<RevenueChartDto> getRevenueChart(Long publisherId, Integer year);
    GameDto updateGameByPublisher(Long publisherId, Long gameId, GameUpdateDto request);
}