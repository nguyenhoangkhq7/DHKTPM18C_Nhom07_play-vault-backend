package fit.iuh.controllers;

import fit.iuh.dtos.GameDTO;
import fit.iuh.models.Game;
import fit.iuh.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @GetMapping
    public List<GameDTO> getGames(@RequestParam(required = false) String categoryName) {
        return gameService.findGamesByCategoryName(categoryName);
    }

    @GetMapping("/top")
    public List<GameDTO> getTopRatedGames(@RequestParam(defaultValue = "5") int limit) {
        return gameService.findTopRatedGames(limit);
    }


}
