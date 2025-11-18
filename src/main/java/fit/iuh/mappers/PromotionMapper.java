package fit.iuh.mappers;

import fit.iuh.dtos.PromotionRequestDto;
import fit.iuh.dtos.PromotionResponseDto;
import fit.iuh.models.Promotion;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    // Entity -> Response DTO
    PromotionResponseDto toDto(Promotion promotion);

    // Request DTO -> Entity (Bỏ qua ID và Publisher vì set sau)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "games", ignore = true)
    Promotion toEntity(PromotionRequestDto dto);

    // Update Entity từ DTO (Bỏ qua ID và Publisher)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "games", ignore = true)
    void updateEntityFromDto(PromotionRequestDto dto, @MappingTarget Promotion promotion);
}