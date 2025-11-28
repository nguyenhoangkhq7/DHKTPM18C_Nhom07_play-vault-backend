package fit.iuh.services;

import fit.iuh.dtos.GameDto;
import fit.iuh.dtos.GameSearchResponseDto;
import fit.iuh.dtos.OrderItemDto;
import fit.iuh.mappers.GameMapper;
import fit.iuh.mappers.OrderItemMapper;
import fit.iuh.models.Game;
import fit.iuh.dtos.GameWithRatingDto;
import fit.iuh.models.OrderItem;
import fit.iuh.repositories.GameRepository;
import fit.iuh.repositories.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    boolean checkOwnership(String username, Long gameId);

    List<GameDto> getAllByGameToday();
}