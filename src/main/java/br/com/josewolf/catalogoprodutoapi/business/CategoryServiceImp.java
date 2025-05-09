package br.com.josewolf.catalogoprodutoapi.business;

import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
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

    @Override
    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Categoria não encontrada com o id: " + id));
        category.setName(categoryDetails.getName());
        category.setProducts(categoryDetails.getProducts());

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if(!categoryRepository.existsById(id))
            throw new RuntimeException("Categoria não encontrada com o ID: " + id);

        categoryRepository.deleteById(id);
    }
}

//private final CategoryRepository categoryRepository; // 2. Dependência do nosso repositório.
//
//// 3. Injeção de dependência via construtor (recomendado).
//@Autowired
//public CategoryServiceImpl(CategoryRepository categoryRepository) {
//    this.categoryRepository = categoryRepository;
//}
//
//@Override
//@Transactional // 4. Indica que este método deve ser executado dentro de uma transação.
//public Category createCategory(Category category) {
//    // Poderíamos adicionar validações aqui antes de salvar
//    // Ex: verificar se já existe uma categoria com o mesmo nome
//    return categoryRepository.save(category);
//}
//
//@Override
//@Transactional(readOnly = true) // 5. readOnly = true é uma otimização para operações de leitura.
//public List<Category> getAllCategories() {
//    return categoryRepository.findAll();
//}
//
//@Override
//@Transactional(readOnly = true)
//public Optional<Category> getCategoryById(Long id) {
//    return categoryRepository.findById(id);
//}
//
//@Override
//@Transactional
//public Category updateCategory(Long id, Category categoryDetails) {
//    Category category = categoryRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Categoria não encontrada com o id: " + id)); // 6. Lançar exceção se não encontrar
//
//    category.setName(categoryDetails.getName());
//    // Se houver outros campos para atualizar, eles seriam definidos aqui.
//
//    return categoryRepository.save(category);
//}
//
//@Override
//@Transactional
//public void deleteCategory(Long id) {
//    if (!categoryRepository.existsById(id)) {
//        throw new RuntimeException("Categoria não encontrada com o id: " + id); // 7. Verificar se existe antes de deletar
//    }
//    categoryRepository.deleteById(id);
//}
