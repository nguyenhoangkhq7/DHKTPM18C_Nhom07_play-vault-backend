package fit.iuh.services;

import fit.iuh.dtos.PublisherDto;
import fit.iuh.models.Publisher;
import org.springframework.stereotype.Service;

import fit.iuh.dtos.*;
import java.util.List;


public interface PublisherService {
    List<PublisherDto> findAll();
    PublisherDto updateProfile(Long id, PublisherDto dto);
    public PublisherDto getProfileByUsername(String username);
    void registerPublisher(PublisherRegisterRequest request);
    PublisherDashboardDto getPublisherDashboardStats(Long publisherId);
    List<RevenueChartDto> getRevenueChart(Long publisherId, Integer year);
    GameDto updateGameByPublisher(Long publisherId, Long gameId, GameUpdateDto request);
}
