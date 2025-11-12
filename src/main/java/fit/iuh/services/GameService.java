package fit.iuh.services;

import fit.iuh.dtos.GameDTO;

import java.util.List;

public interface GameService {
    List<GameDTO> findAll();

    GameDTO findById(Long id);

    List<GameDTO> findGamesByCategoryName(String categoryName);

    List<GameDTO> findTopRatedGames(int topN);
}
