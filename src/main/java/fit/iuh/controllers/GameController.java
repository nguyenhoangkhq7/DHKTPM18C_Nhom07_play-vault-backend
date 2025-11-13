package fit.iuh.controllers;


import fit.iuh.models.Game;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.services.GameBasicInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

@Controller
@RequestMapping("/game")
public class GameController {
    private final GameBasicInfoService gameBasicInfoService;

    public GameController(GameBasicInfoService gameBasicInfoService) {
        this.gameBasicInfoService = gameBasicInfoService;
    }

    // Lấy tất cả thông tin cơ bản của các game yêu thích của khách hàng theo customerId
    @GetMapping("/favorites/{customerId}")
    public List<GameBasicInfo> getFavoriteGames(@PathVariable Long customerId) {
        return gameBasicInfoService.findAllByGameFavoriteWithCustomerId(customerId);
    }

}


