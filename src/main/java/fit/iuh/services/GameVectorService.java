package fit.iuh.services;

import fit.iuh.models.Game;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.models.SystemRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameVectorService {

    private final SimpleVectorStore vectorStore;
    private static final String VECTOR_STORE_FILE = "vector_store.json";


    // 1. Dùng @Async để việc đồng bộ chạy ngầm, không làm treo giao diện người dùng
    @Async
    public void syncAllGames(List<Game> games) {
        log.info("Bắt đầu đồng bộ {} game vào Vector Store...", games.size());

        List<Document> documents = games.stream()
                .filter(this::isValidGame)
                .map(this::convertToDocument)
                .collect(Collectors.toList());

        List<String> idsToRemove = documents.stream()
                .map(Document::getId)
                .collect(Collectors.toList());

        if (!idsToRemove.isEmpty()) {
            vectorStore.delete(idsToRemove);
        }

        vectorStore.add(documents);
        saveToFile();
        log.info("Đã đồng bộ xong!");
    }

    /**
     * 1. Đồng bộ danh sách Game vào Vector Store
     */
    public void addGames(List<Game> games) {
        log.info("🔄 Bắt đầu đồng bộ {} game vào Vector Store...", games.size());

        // 1. Chuyển đổi Game -> Document
        List<Document> documents = games.stream()
                .filter(this::isValidGame)
                .map(this::convertToDocument)
                .collect(Collectors.toList());

        // 2. Lấy danh sách ID để xóa dữ liệu cũ (Tránh trùng lặp)
        List<String> idsToRemove = documents.stream()
                .map(Document::getId)
                .collect(Collectors.toList());

        if (!idsToRemove.isEmpty()) {
            vectorStore.delete(idsToRemove);
        }

        // 3. Thêm mới và lưu file
        vectorStore.add(documents);
        saveToFile();
        log.info("✅ Đã đồng bộ xong!");
    }

    // 2. Tìm kiếm có lọc ngưỡng điểm (Threshold)
    public List<Long> searchGameIds(String query, int topK, double threshold) {
        List<Document> similarDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .similarityThreshold(threshold) // Chỉ lấy kết quả giống trên mức này (vd: 0.5)
                        .build()
        );

        return similarDocs.stream()
                .map(doc -> Long.parseLong(doc.getMetadata().get("gameId").toString()))
                .collect(Collectors.toList());
    }

    // Hàm helper để lưu file
    private void saveToFile() {
        try {
            File file = new File(VECTOR_STORE_FILE);
            vectorStore.save(file);
            System.out.println("💾 Đã lưu Vector Store xuống file thành công!");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lưu file vector: " + e.getMessage());
        }
    }

    // Helper: Tạo nội dung phong phú hơn cho AI học
    // Hàm Helper: Tạo "tấm thẻ căn cước" chi tiết cho Game để AI học
    private Document convertToDocument(Game g) {
        GameBasicInfo info = g.getGameBasicInfos();
        StringBuilder content = new StringBuilder();

        // 1. Tên Game & Giá (Giúp tìm: "Game miễn phí", "Game dưới 500k")
        content.append("Title: ").append(info.getName()).append("\n");
        String priceStr = "Unknown"; // Mặc định nếu giá null
        if (info.getPrice() != null) {
            // Nếu giá = 0 thì ghi là Free, ngược lại ghi số tiền
            if (info.getPrice().compareTo(BigDecimal.ZERO) == 0) {
                priceStr = "Free";
            } else {
                priceStr = info.getPrice() + " đ";
            }
        }
        content.append("Price: ").append(priceStr).append("\n");

        // 2. Thể loại (Giúp tìm: "Game nhập vai", "Game bắn súng")
        if (info.getCategory() != null) {
            content.append("Genre: ").append(info.getCategory().getName()).append("\n");
        }

        // 3. Nhà phát hành (Giúp tìm: "Game của EA", "Game Nintendo")
        if (info.getPublisher() != null) {
            content.append("Publisher: ").append(info.getPublisher().getStudioName()).append("\n");
        }

        // 4. Nền tảng hỗ trợ (Giúp tìm: "Game cho PC", "Game PS5")
        // Giả sử Game có quan hệ Many-to-Many với Platform
        if (info.getPlatforms() != null && !info.getPlatforms().isEmpty()) {
            String platforms = g.getGameBasicInfos().getPlatforms().stream()
                    .map(p -> p.getName()) // Hoặc p.getPlatformName()
                    .collect(Collectors.joining(", "));
            content.append("Platforms: ").append(platforms).append("\n");
        }

        // 5. Cấu hình yêu cầu ( Giúp tìm "Game nhẹ", "Game ram 4GB")
        // Giả sử Game có quan hệ với SystemRequirement (list hoặc 1-1)
        if (info.getSystemRequirement() != null) {

            SystemRequirement req = info.getSystemRequirement();

            if (req != null) {
                content.append("System Specs: ");

                // Nối các thông tin quan trọng vào chuỗi
                if (req.getRam() != null) {
                    content.append("RAM ").append(req.getRam()).append(", ");
                }
                if (req.getGpu() != null) {
                    content.append("GPU ").append(req.getGpu()).append(", ");
                }
                if (req.getCpu() != null) {
                    content.append("CPU ").append(req.getCpu()).append(", ");
                }
                if (req.getStorage() != null) {
                    content.append("Storage ").append(req.getStorage());
                }
                content.append("\n");
            }
            content.append("System Requirements available.\n");
        }

        // 6. Mô tả (Phần hồn của dữ liệu)
        if (info.getShortDescription() != null) {
            content.append("Summary: ").append(info.getShortDescription()).append("\n");
        }
        if (info.getDescription() != null) {
            // Cắt bớt nếu mô tả quá dài (>1000 ký tự) để tiết kiệm token và tránh nhiễu
            String desc = info.getDescription();
            if (desc.length() > 5000) desc = desc.substring(0, 5000);
            content.append("Description: ").append(desc).append("\n");
        }

        // 7. Metadata (ID để truy xuất ngược)
        Map<String, Object> metadata = Map.of(
                "gameId", g.getId().toString()
        );

        return new Document(g.getId().toString(), content.toString(), metadata);
    }

    private boolean isValidGame(Game g) {
        return g.getGameBasicInfos() != null
                && g.getGameBasicInfos().getDescription() != null
                && !g.getGameBasicInfos().getDescription().isEmpty();
    }
}