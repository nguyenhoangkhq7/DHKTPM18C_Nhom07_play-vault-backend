package fit.iuh.services.impl;

import fit.iuh.dtos.GameDTO;
import fit.iuh.dtos.mapper.GameMapper;
import fit.iuh.models.Game;
import fit.iuh.repositories.GameRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GameServiceImpl implements fit.iuh.services.GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper; // <-- inject MapStruct

    @Override
    public List<GameDTO> findAll() {
        return gameRepository.findAll()
                .stream()
                .map(gameMapper::toDTO)
                .toList();
    }

    @Override
    public GameDTO findById(Long id) {
        return gameRepository.findById(id)
                .map(gameMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<GameDTO> findGamesByCategoryName(String categoryName) {

        List<Game> games = (categoryName == null || categoryName.isEmpty())
                ? gameRepository.findAll()
                : gameRepository.findByGameBasicInfos_Category_Name(categoryName);

        return games.stream()
                .map(gameMapper::toDTO)
                .toList();
    }

    @Override
    public List<GameDTO> findTopRatedGames(int topN) {
        return gameRepository.findTopRatedGames(topN)
                .stream()
                .map(gameMapper::toDTO)
                .toList();
    }
}
