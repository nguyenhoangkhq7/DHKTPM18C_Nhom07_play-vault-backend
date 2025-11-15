package fit.iuh.mappers;

import fit.iuh.dtos.CategoryDto;
import fit.iuh.models.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDTO(Category category);

    Category toEntity(CategoryDto dto);

    List<CategoryDto> toDTOs(List<Category> categories);
}
