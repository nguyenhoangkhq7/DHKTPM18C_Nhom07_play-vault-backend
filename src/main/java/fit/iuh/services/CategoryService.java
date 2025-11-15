package fit.iuh.services;

import fit.iuh.dtos.CategoryDto;
import fit.iuh.models.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll();
}
