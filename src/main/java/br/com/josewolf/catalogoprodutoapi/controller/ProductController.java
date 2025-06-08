package br.com.josewolf.catalogoprodutoapi.controller;

import br.com.josewolf.catalogoprodutoapi.business.ProductService;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.ProductPatchRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.ProductRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.ProductResponseDTO;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Produto", description = "Cadastro e gerenciamento de produtos.")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Criação de um novo produto", description = "Deve criar um produto com sucesso.")
    @ApiResponse(responseCode = "201", description = "Produto criado com suceso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO){
        ProductResponseDTO createProductDTO = productService.createProduct(productRequestDTO);
        return new ResponseEntity<>(createProductDTO, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lista todos os produtos.", description = "Lista todos os produtos.")
    @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(){
        List<ProductResponseDTO> productList = productService.getAllProducts();
        return ResponseEntity.ok(productList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lista um produto.", description = "Lista um produto com base no identificador.")
    @ApiResponse(responseCode = "200", description = "Produto encontrado")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id){
        Optional<ProductResponseDTO> productOptional = productService.getProductById(id);
        return productOptional
                .map(product -> ResponseEntity.ok(product))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um produto.", description = "Atualiza um produto com base em um identificador.")
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos para atualização")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado para atualização")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id,@Valid @RequestBody ProductRequestDTO productRequestDTO){
        ProductResponseDTO updateProduct = productService.updateProduct(id,productRequestDTO);
        return ResponseEntity.ok(updateProduct);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza parcialmente um produto.", description = "Atualiza apenas os campos fornecidos de um produto com base no seu identificador.")
    @ApiResponse(responseCode = "200", description = "Produto atualizado parcialmente com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (ex: formato, tamanho, valor)")
    @ApiResponse(responseCode = "404", description = "Produto ou Categoria associada não encontrada")
    public ResponseEntity<ProductResponseDTO> patchProduct(@PathVariable Long id,
            @Valid @RequestBody ProductPatchRequestDTO productPatchRequestDTO) {
        ProductResponseDTO updatedProduct = productService.patchProduct(id, productPatchRequestDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um produto.", description = "Deleta um produto específico com base no identificador.")
    @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
