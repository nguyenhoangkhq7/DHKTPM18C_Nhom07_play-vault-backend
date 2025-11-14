package fit.iuh.mappers;

import fit.iuh.dtos.GameCardDto;
import fit.iuh.models.Game;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper tự động chuyển Game Entity sang GameCardDto.
 */
@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(source = "gameBasicInfos.name", target = "name")
    @Mapping(source = "gameBasicInfos.thumbnail", target = "thumbnail")
    @Mapping(source = "gameBasicInfos.price", target = "price")
    GameCardDto toDto(Game game);
}
