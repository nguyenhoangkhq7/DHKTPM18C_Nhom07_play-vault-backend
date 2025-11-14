package fit.iuh.dtos;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class GameBasicInfoDto {
    private Long id;
    private String name;
    private String shortDescription;
    private String description;
    private BigDecimal price;
    private String thumbnail;
    private String trailerUrl;
    private Integer requiredAge;
    private Boolean isSupportController;
    
    // Thêm 2 trường này để Mapper map vào
    private String categoryName;
    private String publisherName;
}