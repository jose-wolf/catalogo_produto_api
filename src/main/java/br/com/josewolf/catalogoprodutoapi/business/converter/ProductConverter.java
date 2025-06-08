package br.com.josewolf.catalogoprodutoapi.business.converter;

import br.com.josewolf.catalogoprodutoapi.business.dto.request.ProductPatchRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.ProductRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.ProductResponseDTO;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductConverter {

    private final CategoryConverter categoryConverter;

    public ProductResponseDTO toResponseDTO(Product product){
        if(product == null) {
            return null;
        }
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .description(product.getDescription())
                .category(categoryConverter.toCategory(product.getCategory()))
                .build();
    }

    public Product toEntity(ProductRequestDTO productRequestDTO, Category category){
        if(productRequestDTO == null){
            return null;
        }
        return Product.builder()
                .name(productRequestDTO.getName())
                .description(productRequestDTO.getDescription())
                .price(productRequestDTO.getPrice())
                .stockQuantity(productRequestDTO.getStockQuantity())
                .category(category)
                .build();
    }

    public List<ProductResponseDTO> toResponseDTOList (List<Product> products){
        if(products == null){
            return Collections.emptyList();
        }
        return products.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromRequestDTO(ProductRequestDTO productRequestDTO, Product entityToUpdate, Category category){
        if(productRequestDTO == null || entityToUpdate == null){
            return;
        }

        entityToUpdate.setName(productRequestDTO.getName() != null ? productRequestDTO.getName() : entityToUpdate.getName());
        entityToUpdate.setDescription(productRequestDTO.getDescription() != null ? productRequestDTO.getDescription() :
                entityToUpdate.getDescription());
        entityToUpdate.setPrice(productRequestDTO.getPrice() != null ? productRequestDTO.getPrice() :
                entityToUpdate.getPrice());
        entityToUpdate.setStockQuantity(productRequestDTO.getStockQuantity() != null ? productRequestDTO.getStockQuantity() :
                entityToUpdate.getStockQuantity());

        if (category != null && (entityToUpdate.getCategory() == null || !entityToUpdate.getCategory().getId().equals(category.getId()))) {
            entityToUpdate.setCategory(category);
        }
    }

    public void patchEntityFromDTO(ProductPatchRequestDTO patchDTO, Product entityToUpdate, Category categoryIfChanged) {
        if (patchDTO == null || entityToUpdate == null) {
            return;
        }
        if (patchDTO.getName() != null) {
            entityToUpdate.setName(patchDTO.getName());
        }
        if (patchDTO.getDescription() != null) {
            entityToUpdate.setDescription(patchDTO.getDescription());
        }
        if (patchDTO.getPrice() != null) {
            entityToUpdate.setPrice(patchDTO.getPrice());
        }
        if (patchDTO.getStockQuantity() != null) {
            entityToUpdate.setStockQuantity(patchDTO.getStockQuantity());
        }

        if (categoryIfChanged != null) {
            if (entityToUpdate.getCategory() == null || !entityToUpdate.getCategory().getId().equals(categoryIfChanged.getId())) {
                entityToUpdate.setCategory(categoryIfChanged);
            }
        }
    }
}
