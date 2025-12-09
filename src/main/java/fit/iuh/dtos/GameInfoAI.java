package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class GameInfoAI {
    private Long id;
    private String name;
    private String shortDescription;
    private String description;
    private BigDecimal price;
    private String thumbnail;
    private String trailerUrl;
    private Integer requiredAge;
}
