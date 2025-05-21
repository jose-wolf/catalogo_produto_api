package br.com.josewolf.catalogoprodutoapi.business.dto.response;

import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private CategoryResponseDTO category;

}
