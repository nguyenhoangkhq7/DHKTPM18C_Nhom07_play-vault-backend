package fit.iuh.tools;

import fit.iuh.dtos.*;
import fit.iuh.models.enums.Os;
import fit.iuh.services.GameBasicInfoService;
import org.springframework.ai.tool.annotation.Tool;

import java.util.List;

public class GameTools {

    private final GameBasicInfoService gameBasicInfoService;

    public GameTools(GameBasicInfoService gameBasicInfoService) {
        this.gameBasicInfoService = gameBasicInfoService;
    }

    // ---------------------- HÀM CHUẨN HOÁ OS ----------------------
    private Os normalizeOS(String os) {
        if (os == null) return Os.WINDOWS;

        String s = os.toLowerCase();

        if (s.contains("win")) return Os.WINDOWS;
        if (s.contains("mac") || s.contains("osx")) return Os.MACBOOK;
        if (s.contains("linux") || s.contains("ubuntu") || s.contains("debian")) return Os.LINUX;

        return Os.WINDOWS; // fallback mặc định
    }

    // ---------------------- 1. Tìm theo tên ----------------------
    @Tool(description = "Tìm thông tin của game theo tên game.")
    public List<GameInfoAI> findByNameGame(String keyword) {
        return gameBasicInfoService.findInfosByName(keyword);
    }

    // ---------------------- 2. Tìm theo thể loại ----------------------
    @Tool(description = "Tìm thông tin của các game theo thể loại. Nếu nhiều game thì lấy top 5.")
    public List<GameInfoAI> findByTypeGame(String keyword) {
        return gameBasicInfoService.findByTypeGame(keyword);
    }

    // ---------------------- 3. Gợi ý theo cấu hình ----------------------
    @Tool(description = "Tư vấn game phù hợp cấu hình máy (OS, CPU, GPU, RAM, Storage). Nếu nhiều game thì lấy top 5.")
    public List<GameInfoAI> suggestBySystem(SystemSpecs specs) {

        Os os = normalizeOS(specs.os());

        int ram = specs.ramGB() != null ? specs.ramGB() : 0;
        int storage = specs.storageGB() != null ? specs.storageGB() : 0;

        return gameBasicInfoService.findBySystem(
                os.name(),                 // enum -> String
                specs.cpu(),
                specs.gpu(),
                ram,
                storage
        );
    }

    // ---------------------- 4. Tìm kiếm nâng cao ----------------------
    @Tool(description = "Tìm game nâng cao theo cấu hình, từ khóa, thể loại, giá, rating. Nếu nhiều game thì lấy top 5.")
    public List<GameInfoAI> searchAdvanced(GameSearchFilter filter) {

        Os os = normalizeOS(filter.os());

        return gameBasicInfoService.searchAdvanced(
                os.name(),
                filter.cpu(),
                filter.gpu(),
                filter.ramGB() != null ? filter.ramGB() : 0,
                filter.storageGB() != null ? filter.storageGB() : 0,
                filter.keyword(),
                filter.categoryName(),
                filter.minRating() != null ? filter.minRating() : 0.0,
                filter.maxPrice() != null ? filter.maxPrice() : Double.MAX_VALUE
        );
    }

    // ---------------------- 5. Kiểm tra tương thích game ----------------------
    @Tool(description = "Kiểm tra cấu hình máy có chạy được game theo tên không.")
    public boolean checkGameCompatibility(String nameGame, SystemSpecs specs) {

        Os os = normalizeOS(specs.os());

        int ram = specs.ramGB() != null ? specs.ramGB() : 0;
        int storage = specs.storageGB() != null ? specs.storageGB() : 0;

        return gameBasicInfoService.testNameGameAndSystem(
                nameGame,
                os.name(),
                specs.cpu(),
                specs.gpu(),
                ram,
                storage
        );
    }
}
