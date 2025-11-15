package fit.iuh.services.impl;

import fit.iuh.dtos.CategoryDto;
import fit.iuh.mappers.CategoryMapper;
import fit.iuh.models.Category;
import fit.iuh.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements fit.iuh.services.CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll() {
        return categoryMapper.toDTOs(categoryRepository.findAll());
    }
}
