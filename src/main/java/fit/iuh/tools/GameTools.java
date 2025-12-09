package fit.iuh.tools;

import fit.iuh.dtos.*; // <-- Đảm bảo import các Record mới tạo
import fit.iuh.services.GameBasicInfoService;

import org.springframework.ai.tool.annotation.Tool;
import java.util.List;

public class GameTools {

    private GameBasicInfoService gameBasicInfoService;

    public GameTools(GameBasicInfoService gameBasicInfoService) {
        this.gameBasicInfoService = gameBasicInfoService;
    }
    // ... Constructor giữ nguyên ...

    // ---------------------- 1. Tìm theo tên (Giữ nguyên) ----------------------
    @Tool(description = "Tìm thông tin của game theo tên game.")
    public List<GameInfoAI> findByNameGame(String keyword) {
        return gameBasicInfoService.findInfosByName(keyword);
    }

    @Tool(description = "Tìm thông tin của các game theo thể loại nếu nhiều game thì lấy top 5 game.")
    public List<GameInfoAI> findByTypeGame(String keyword) {
        return gameBasicInfoService.findByTypeGame(keyword);
    }

    // ---------------------- 2. Gợi ý theo cấu hình (SỬA LỚN) ----------------------
    @Tool(description = "Tư vấn game phù hợp cấu hình máy. Inputs: Các thông số cấu hình máy (OS, CPU, GPU, RAM, Storage).nếu nhiều game thì lấy top 5 game.")
    public List<GameInfoAI> suggestBySystem(SystemSpecs specs) { // <-- Chỉ còn 1 tham số
        // Logic xử lý null được thực hiện tại đây, sử dụng specs.ramGB(), specs.cpu()
        int finalRam = (specs.ramGB() != null) ? specs.ramGB() : 0;
        int finalStorage = (specs.storageGB() != null) ? specs.storageGB() : 0;

        return gameBasicInfoService.findBySystem(
                specs.os().name(), specs.cpu(), specs.gpu(), finalRam, finalStorage
        );
    }

    // ---------------------- 3. Tìm kiếm nâng cao (SỬA LỚN) ----------------------
    @Tool(description = "Tìm kiếm game nâng cao dựa trên tiêu chí cấu hình, từ khóa, thể loại và giá. nếu nhiều game thì lấy top 5 game.")
    public List<GameInfoAI> searchAdvanced(GameSearchFilter filter) { // <-- Chỉ còn 1 tham số

        return gameBasicInfoService.searchAdvanced(
                filter.os(), filter.cpu(), filter.gpu(),
                filter.ramGB() != null ? filter.ramGB() : 0,
                filter.storageGB() != null ? filter.storageGB() : 0,
                filter.keyword(), filter.categoryName(),
                filter.minRating() != null ? filter.minRating() : 0.0,
                filter.maxPrice() != null ? filter.maxPrice() : Double.MAX_VALUE
        );
    }

    // ---------------------- 4. Kiểm tra tương thích (SỬA LỚN) ----------------------
    @Tool(description = "Kiểm tra tương thích game theo tên game và các thông số cấu hình máy.")
    public boolean checkGameCompatibility(String nameGame, SystemSpecs specs) { // <-- 2 tham số (Tên game và Object cấu hình)

        int finalRam = (specs.ramGB() != null) ? specs.ramGB() : 0;
        int finalStorage = (specs.storageGB() != null) ? specs.storageGB() : 0;

        return gameBasicInfoService.testNameGameAndSystem(
                nameGame, specs.os().name(), specs.cpu(), specs.gpu(),
                finalRam, finalStorage
        );
    }
}