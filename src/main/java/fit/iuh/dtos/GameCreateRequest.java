package fit.iuh.dtos;

import lombok.Data;
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
}
