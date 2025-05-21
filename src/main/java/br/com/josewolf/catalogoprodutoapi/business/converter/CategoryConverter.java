package br.com.josewolf.catalogoprodutoapi.business.converter;

import br.com.josewolf.catalogoprodutoapi.business.dto.request.CategoryRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTO;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CategoryConverter {

    public CategoryResponseDTO toCategory(Category category){
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toCategoryEntity(CategoryRequestDTO categoryRequestDTO){
        return Category.builder()
                .name(categoryRequestDTO.getName())
                .build();
    }

    public List<Category> toCategoryEntityList(List<CategoryRequestDTO> categoryRequestDTOS){
        if(categoryRequestDTOS == null){
            return Collections.emptyList();
        }

        List<Category> categories = new ArrayList<>();
        for(CategoryRequestDTO categoryDTO : categoryRequestDTOS){
            categories.add(toCategoryEntity(categoryDTO));
        }
        return categories;
    }

    public List<CategoryResponseDTO> toListCategoryDTO(List<Category> categories){
        if (categories == null){
            return Collections.emptyList();
        }
        return categories.stream().map(this::toCategory).toList();
    }

    public void updateCategory(CategoryRequestDTO categoryRequestDTO, Category categoryEntity){
        if(categoryRequestDTO == null || categoryEntity == null){
            return;
        }

        categoryEntity.setName(categoryRequestDTO.getName() != null ? categoryRequestDTO.getName() : categoryEntity.getName());
    }
}
