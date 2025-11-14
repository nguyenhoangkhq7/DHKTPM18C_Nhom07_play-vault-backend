package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameBasicInforDTO {
    private Long id;
    private String name;
    private String shortDescription;
    private String description;
    private BigDecimal price;
    private String filePath;
    private String thumbnail;
    private String trailerUrl;
    private Integer requiredAge;
    private Boolean isSupportController;
}
