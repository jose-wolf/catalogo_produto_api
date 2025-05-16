package br.com.josewolf.catalogoprodutoapi.controller;

import br.com.josewolf.catalogoprodutoapi.business.CategoryService;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> createCategpry(@RequestBody Category category){
        Category createCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(createCategory, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id){
        Optional<Category> categoryOptional = categoryService.getCategoryById(id);
        return categoryOptional
                .map(category -> ResponseEntity.ok(category))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails){
        try {
            Category updateCategory = categoryService.updateCategory(id, categoryDetails);
            return ResponseEntity.ok(updateCategory);
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}
//@PostMapping
//public ResponseEntity<Category> createCategory(@RequestBody Category category) {
//    // @RequestBody: Diz ao Spring para converter o corpo da requisição JSON em um objeto Category.
//    // Validações podem ser adicionadas aqui ou no DTO/Entidade.
//
//    Category createdCategory = categoryService.createCategory(category);
//
//    // Retorna a categoria criada e o status HTTP 201 (Created).
//    // O URI do recurso criado também pode ser retornado no header "Location",
//    // mas vamos manter simples por enquanto.
//    return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
//}

