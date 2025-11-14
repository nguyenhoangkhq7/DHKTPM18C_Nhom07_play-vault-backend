package fit.iuh.dtos;

import fit.iuh.models.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private Long id;
    private LocalDate releaseDate;
    private GameBasicInforDTO gameBasicInfos;
    private String categoryName;

}

