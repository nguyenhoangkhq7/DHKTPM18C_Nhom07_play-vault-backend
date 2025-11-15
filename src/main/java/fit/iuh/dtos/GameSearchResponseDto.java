// trong package fit.iuh.dtos
package fit.iuh.dtos;

import fit.iuh.models.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor // Thêm constructor cho dễ map
public class GameSearchResponseDto {
    private Long id;
    private String name;
    private String thumbnail;
    private BigDecimal price;
    private String categoryName;

    // Tạo một hàm static để chuyển đổi (mapping)
    public static GameSearchResponseDto fromEntity(Game game) {
        // Lấy thông tin từ lazy-loading
        String gameName = game.getGameBasicInfos().getName();
        String gameThumb = game.getGameBasicInfos().getThumbnail();
        BigDecimal gamePrice = game.getGameBasicInfos().getPrice();
        String catName = game.getGameBasicInfos().getCategory().getName();

        return new GameSearchResponseDto(game.getId(), gameName, gameThumb, gamePrice, catName);
    }
}