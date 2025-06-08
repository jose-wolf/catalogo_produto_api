package br.com.josewolf.catalogoprodutoapi.controller;

import br.com.josewolf.catalogoprodutoapi.business.CategoryService;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.CategoryRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTO;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category", description = "Cadastro e gerenciamento de Categorias.")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Cria uma nova categoria.", description = "Cria um novo registro de categoria no sistema.")
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<CategoryResponseDTO> createCategpry(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO){
        CategoryResponseDTO createCategoryDTO = categoryService.createCategory(categoryRequestDTO);
        return new ResponseEntity<>(createCategoryDTO, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lista todas as categorias.", description = "Lista todas as categorias.")
    @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories(){
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lista uma categoria.", description = "Lista uma categoria com base de um identificador.")
    @ApiResponse(responseCode = "200", description = "Categoria encontrada")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id){
        Optional<CategoryResponseDTO> categoryOptional = categoryService.getCategoryById(id);
        return categoryOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma categoria.", description = "Atualiza uma categoria com base em um identificador.")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos para atualização")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada para atualização")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO categoryDetails){
        CategoryResponseDTO updateCategory = categoryService.updateCategory(id, categoryDetails);
        return ResponseEntity.ok(updateCategory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma categoria.", description = "Deleta uma categoria específica com base no identificador.")
    @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}


