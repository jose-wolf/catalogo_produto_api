package br.com.josewolf.catalogoprodutoapi.business.dto.response;

import java.math.BigDecimal;

public class ProductResponseDTOFixture {

    public static ProductResponseDTO build(Long id,
                                     String name,
                                     String description,
                                     BigDecimal price,
                                     Integer stockQuantity,
                                     CategoryResponseDTO category){
        return new ProductResponseDTO(id,name,description,price,stockQuantity,category);
    }

}
