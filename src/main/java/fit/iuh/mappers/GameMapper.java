package fit.iuh.mappers;

import fit.iuh.dtos.GameBasicInfoDto;
import fit.iuh.models.Game;
import org.mapstruct.*;


import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface GameMapper {
    /**
     * Chuyển Game (Model) -> GameBasicInfoDto (DTO)
     * Tất cả dữ liệu đều nằm trong "game.gameBasicInfos"
     */
    @Named("toBasicInfoDto")
    @Mapping(source = "gameBasicInfos.name", target = "name")
    @Mapping(source = "gameBasicInfos.shortDescription", target = "shortDescription")
    @Mapping(source = "gameBasicInfos.description", target = "description")
    @Mapping(source = "gameBasicInfos.price", target = "price")
    @Mapping(source = "gameBasicInfos.thumbnail", target = "thumbnail")
    @Mapping(source = "gameBasicInfos.trailerUrl", target = "trailerUrl")
    @Mapping(source = "gameBasicInfos.requiredAge", target = "requiredAge")
    @Mapping(source = "gameBasicInfos.isSupportController", target = "isSupportController")
    @Mapping(source = "gameBasicInfos.category.name", target = "categoryName")
    @Mapping(source = "gameBasicInfos.publisher.studioName", target = "publisherName")
    GameBasicInfoDto toBasicInfoDto(Game game); // Input là Game

    // Map list Game (Tự động dùng phương thức toBasicInfoDto ở trên)
    List<GameBasicInfoDto> toDtoList(List<Game> games);

}