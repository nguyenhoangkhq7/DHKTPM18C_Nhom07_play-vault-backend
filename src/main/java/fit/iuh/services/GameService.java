package fit.iuh.services;

import fit.iuh.dtos.*;
import fit.iuh.models.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GameService {
    List<GameDto> findAll();
    GameDto findById(Long id);
    List<GameDto> findGamesByCategoryName(String categoryName);
    List<GameDto> findTopRatedGames(int topN);
    Game findGameEntityById(Long id);
    List<GameWithRatingDto> getTopGamesWithRating(int topN);

    // Thêm method tìm kiếm nâng cao
    Page<GameSearchResponseDto> searchAndFilterGames(
            String keyword,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Pageable pageable);

    GameWithRatingDto getGameWithRatingById(Long id);

    Page<GameSearchResponseDto> searchAndFilterPendingGames(Pageable pageable, String searchQuery);

    Page<GameSearchResponseDto> searchAndFilterApprovedGames(Pageable pageable, String searchQuery, String categoryFilter);

    GameDetailDto getGameForAdmin(Long gameId);

    GameDetailDto approveGame(Long gameId);

    GameDetailDto rejectGame(Long gameId);

    GameDetailDto updateApprovedGameStatus(Long gameId, String newStatus);

    List<GameDto> getGamesByPublisherId(Long publisherId);

    DashboardStatsResponse getDashboardStats();
}