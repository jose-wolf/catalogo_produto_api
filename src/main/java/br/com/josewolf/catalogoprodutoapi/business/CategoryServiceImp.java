package br.com.josewolf.catalogoprodutoapi.business;

import br.com.josewolf.catalogoprodutoapi.business.converter.CategoryConverter;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.CategoryRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTO;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.exceptions.ResourceNotFoundException;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO){
        Category categoryEntity = categoryConverter.toCategoryEntity(categoryRequestDTO);
        Category savedCategory = categoryRepository.save(categoryEntity);
        return categoryConverter.toCategory(savedCategory);
    }

    private CategoryResponseDTO toCategoryResponseDto(Category category) {
        return new CategoryResponseDTO(category.getId(), category.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryConverter.toListCategoryDTO(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryResponseDTO> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryConverter::toCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Categoria não encontrada com o id: " + id));

        categoryConverter.updateCategory(categoryRequestDTO, category);
        Category updateCategory = categoryRepository.save(category);

        return categoryConverter.toCategory(updateCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if(!categoryRepository.existsById(id)){
            throw new ResourceNotFoundException("Categoria não encontrada com o ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

}
