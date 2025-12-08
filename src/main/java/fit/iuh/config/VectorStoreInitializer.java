package fit.iuh.config;

import fit.iuh.models.Game;
import fit.iuh.repositories.GameRepository;
import fit.iuh.services.GameVectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate; // 1. Import này

import java.io.File;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class VectorStoreInitializer {

    private final GameVectorService gameVectorService;
    private final GameRepository gameRepository;
    private final TransactionTemplate transactionTemplate; // 2. Inject TransactionTemplate

    // Tên file phải khớp với cấu hình trong VectorStoreConfig
    private static final String VECTOR_STORE_FILE = "vector_store.json";

    @Bean
    public CommandLineRunner initVectorStore() {
        return args -> {
            File file = new File(VECTOR_STORE_FILE);

            if (!file.exists() || file.length() == 0) {
                log.info("🚀 Phát hiện chưa có file Vector Store. Đang tự động đồng bộ...");

                // 3. Bọc logic trong transactionTemplate.execute(...)
                // Điều này đảm bảo Session vẫn mở khi truy cập vào các thuộc tính Lazy (như description)
                transactionTemplate.execute(status -> {
                    List<Game> allGames = gameRepository.findAll();

                    if (allGames.isEmpty()) {
                        log.warn("⚠️ Database chưa có game nào để đồng bộ.");
                        return null;
                    }

                    // Gọi hàm đồng bộ
                    gameVectorService.addGames(allGames);
                    return null;
                });

                log.info("✅ Tự động đồng bộ hoàn tất!");
            } else {
                log.info("✅ File Vector Store đã tồn tại. Bỏ qua bước đồng bộ ban đầu.");
            }
        };
    }
}