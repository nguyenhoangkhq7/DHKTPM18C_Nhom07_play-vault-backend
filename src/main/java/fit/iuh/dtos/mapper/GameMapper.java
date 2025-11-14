package fit.iuh.dtos.mapper;

import fit.iuh.dtos.GameDTO;
import fit.iuh.models.Game;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    @Mapping(source = "gameBasicInfos", target = "gameBasicInfos")
    @Mapping(source = "gameBasicInfos.category.name", target = "categoryName")
    GameDTO toDTO(Game game);

    @Mapping(source = "gameBasicInfos", target = "gameBasicInfos")
    Game toEntity(GameDTO dto);
}

