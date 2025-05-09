package br.com.josewolf.catalogoprodutoapi.business;

import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category createCategory(Category category);

    List<Category> getAllCategories();

    Optional<Category> getCategoryById(Long id); // Retornar Optional é uma boa prática

    Category updateCategory(Long id, Category categoryDetails);

    void deleteCategory(Long id);

}
