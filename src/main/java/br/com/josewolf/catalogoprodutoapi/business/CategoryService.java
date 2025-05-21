package br.com.josewolf.catalogoprodutoapi.business;

import br.com.josewolf.catalogoprodutoapi.business.dto.request.CategoryRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTO;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO);

    List<CategoryResponseDTO> getAllCategories();

    Optional<CategoryResponseDTO> getCategoryById(Long id); // Retornar Optional é uma boa prática

    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO);

    void deleteCategory(Long id);

}
