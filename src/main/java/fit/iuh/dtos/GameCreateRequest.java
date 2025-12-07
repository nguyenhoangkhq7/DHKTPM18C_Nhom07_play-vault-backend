package fit.iuh.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameCreateRequest {

    @NotBlank
    private String name;

    private String shortDescription;

    // "Ghi chú phát hành" -> map vào game_basic_infos.description
    private String description;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal price;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    private String trailerUrl;

    @NotNull
    private Long categoryId;                 // combobox thể loại

    @PositiveOrZero
    private Integer requiredAge;

    @NotNull
    private Boolean isSupportController;

    // Gửi ID platform (vd [1] = PC) thay vì "WINDOWS"
    @NotNull
    @Size(min = 1)
    private List<Long> platformIds;

    private String filePath;   // ví dụ: "/files/shadow_runner_ex.zip" hoặc URL Drive

    private String thumbnail;

    @NotNull
    private SystemRequirementDTO systemRequirement;

    // Link có sẵn từ client (Drive link thường), sẽ convert sang lh3
    private List<String> gallery;

    // File upload nhiều ảnh
    private List<MultipartFile> galleryFiles;
}
