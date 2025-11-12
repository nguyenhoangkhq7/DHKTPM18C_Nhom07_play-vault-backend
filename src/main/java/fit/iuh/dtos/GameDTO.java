package fit.iuh.dtos;

import fit.iuh.models.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String thumbnail;
    private String categoryName;

    // static mapping method
    public static GameDTO fromEntity(Game game) {
        if (game == null || game.getGameBasicInfos() == null) return null;

        GameDTO dto = new GameDTO();
        dto.setId(game.getId());
        dto.setName(game.getGameBasicInfos().getName());
        dto.setPrice(game.getGameBasicInfos().getPrice());
        dto.setThumbnail(game.getGameBasicInfos().getThumbnail());

        if (game.getGameBasicInfos().getCategory() != null) {
            dto.setCategoryName(game.getGameBasicInfos().getCategory().getName());
        }
        return dto;
    }
}

