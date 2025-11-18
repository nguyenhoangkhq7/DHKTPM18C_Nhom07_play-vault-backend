package fit.iuh.mappers;

import fit.iuh.dtos.GameBasicInfoDto;
import fit.iuh.dtos.GameCardDto;
import fit.iuh.dtos.GameDto;
import fit.iuh.models.Game;
import fit.iuh.models.GameBasicInfo;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GameMapper {

    /**
     * Dùng để map Game -> GameBasicInfoDto
     * @Named("toBasicInfoDto") rất quan trọng để CartMapper có thể gọi
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

    List<GameBasicInfoDto> toDtoList(List<GameBasicInfo> games);

    @Mapping(source = "gameBasicInfos.name", target = "name")
    @Mapping(source = "gameBasicInfos.thumbnail", target = "thumbnail")
    @Mapping(source = "gameBasicInfos.price", target = "price")
    GameCardDto toCardDto(Game game);

    @Mapping(source = "gameBasicInfos", target = "gameBasicInfos")
    @Mapping(source = "gameBasicInfos.category.name", target = "categoryName")
    @Mapping(source = "gameBasicInfos.publisher.studioName", target = "publisherName")
    GameDto toDTO(Game game);

    // Toàn bộ logic map CartItemDto đã được XÓA khỏi đây
}