package fit.iuh.services;

import fit.iuh.dtos.GameDTO;
import fit.iuh.dtos.GameSearchResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GameService {
    List<GameDTO> findAll();
    GameDTO findById(Long id);
    List<GameDTO> findGamesByCategoryName(String categoryName);
    List<GameDTO> findTopRatedGames(int topN);

    // Thêm method tìm kiếm nâng cao
    Page<GameSearchResponseDTO> searchAndFilterGames(
            String keyword,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Pageable pageable);
}