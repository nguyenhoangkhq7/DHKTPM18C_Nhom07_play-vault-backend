package fit.iuh.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Data
public class GameCreateRequest {
    private String title;
    private String summary;
    private String description;
    private Double price;
    private String coverUrl;
    private String trailerUrl;
    private boolean isFree;
    private boolean isSupportController;
    private boolean isAge18;
    private LocalDate releaseDate;
    private List<String> platforms; // ví dụ: ["WINDOWS", "MACBOOK"]
    private String filePath;        // link build trên Google Drive
    private Long categoryId;
    // THÊM ĐOẠN NÀY ĐỂ NHẬN YÊU CẦU CẤU HÌNH TỪ FRONTEND
    // ------------------------------------------------------------------
    private SystemRequirementDto systemRequirement;

    // Inner class (có thể tách riêng thành file khác nếu thích)
    @Getter
    @Setter
    public static class SystemRequirementDto {
        private String cpu;      // ví dụ: "Intel i5-8400 / AMD Ryzen 5 2600"
        private String gpu;      // ví dụ: "NVIDIA GTX 1060 6GB / AMD RX 580"
        private String ram;      // ví dụ: "8 GB"
        private String storage;  // ví dụ: "20 GB"

        // (Tương lai) nếu muốn hỗ trợ nhiều OS
        // private String os; // "WINDOWS", "MACOS", "LINUX"
    }
}
