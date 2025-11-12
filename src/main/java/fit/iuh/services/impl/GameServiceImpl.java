package fit.iuh.services.impl;

import fit.iuh.dtos.GameDTO;
import fit.iuh.models.Game;
import fit.iuh.repositories.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements fit.iuh.services.GameService {
    private final GameRepository gameRepository;

    @Override
    public List<GameDTO> findAll() {
        return gameRepository.findAll()
                .stream()
                .map(GameDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public GameDTO findById(Long id) {
        return gameRepository.findById(id)
                .map(GameDTO::fromEntity)
                .orElse(null);
    }

    @Override
    public List<GameDTO> findGamesByCategoryName(String categoryName) {
        List<Game> games;
        if(categoryName == null || categoryName.isEmpty()) {
            games = gameRepository.findAll();
        } else {
            games = gameRepository.findByGameBasicInfos_Category_Name(categoryName);
        }
        return games.stream()
                .map(GameDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> findTopRatedGames(int topN){
        return gameRepository.findTopRatedGames(topN)
                .stream()
                .map(GameDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
