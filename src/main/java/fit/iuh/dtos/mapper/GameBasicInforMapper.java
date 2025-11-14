package fit.iuh.dtos.mapper;


import fit.iuh.dtos.GameBasicInforDTO;
import fit.iuh.models.GameBasicInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameBasicInforMapper {

    GameBasicInforMapper INSTANCE = Mappers.getMapper(GameBasicInforMapper.class);
    GameBasicInforDTO toDTO(GameBasicInfo entity);

}
