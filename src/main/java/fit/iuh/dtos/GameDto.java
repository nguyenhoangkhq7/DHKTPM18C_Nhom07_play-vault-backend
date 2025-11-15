package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDto {
    private Long id;
    private LocalDate releaseDate;
    private GameBasicInfoDto gameBasicInfos;
    private String categoryName;

}

