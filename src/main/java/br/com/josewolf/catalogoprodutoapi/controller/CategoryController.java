package br.com.josewolf.catalogoprodutoapi.controller;

import br.com.josewolf.catalogoprodutoapi.business.CategoryService;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.CategoryRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTO;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import jakarta.validation.Valid;
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
    public ResponseEntity<CategoryResponseDTO> createCategpry(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO){
        CategoryResponseDTO createCategoryDTO = categoryService.createCategory(categoryRequestDTO);
        return new ResponseEntity<>(createCategoryDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories(){
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id){
        Optional<CategoryResponseDTO> categoryOptional = categoryService.getCategoryById(id);
        return categoryOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO categoryDetails){
        try {
            CategoryResponseDTO updateCategory = categoryService.updateCategory(id, categoryDetails);
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


