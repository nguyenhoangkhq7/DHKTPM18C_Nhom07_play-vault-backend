package fit.iuh.dtos;

import fit.iuh.models.Category;
import fit.iuh.models.Publisher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameWithRatingDto {
    private Long id;
    private LocalDate releaseDate;
    private GameBasicInfoDto gameBasicInfos;
    private String categoryName;
    private Long publisherId;
    private String publisherName;
    private Double avgRating;
    private Integer reviewCount;
    private List<String> platforms;
    private List<String> previewImages;
    private Double discount;
//new
    private SystemRequirementDTO systemRequirement;
}
