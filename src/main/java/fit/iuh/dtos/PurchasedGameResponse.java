package fit.iuh.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchasedGameResponse {
    @NotNull(message = "Game ID cannot be null")
    private Long gameId;

    @NotBlank(message = "Game name cannot be empty")
    private String gameName;

    @NotBlank(message = "Publisher name cannot be empty")
    private String publisherName;

    private String thumbnail;     // URL ảnh

    @NotBlank(message = "Category name cannot be empty")
    private String categoryName;  // Thể loại

    @NotNull(message = "Required age cannot be null")
    @Min(value = 0, message = "Age must be positive")
    private Integer requiredAge;  // Độ tuổi (18+)
}