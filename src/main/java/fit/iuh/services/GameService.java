package fit.iuh.services;

import fit.iuh.dtos.GameDto;
import fit.iuh.dtos.GameSearchResponseDto;
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
    // Thêm method tìm kiếm nâng cao
    Page<GameSearchResponseDto> searchAndFilterGames(
            String keyword,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Pageable pageable);
}