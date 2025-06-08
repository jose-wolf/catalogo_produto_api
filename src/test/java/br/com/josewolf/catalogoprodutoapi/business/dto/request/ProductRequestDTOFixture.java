package br.com.josewolf.catalogoprodutoapi.business.dto.request;

import java.math.BigDecimal;

public class ProductRequestDTOFixture {

    public static ProductRequestDTO build(String name,
                                    String description,
                                    BigDecimal price,
                                    Integer stockQuantity,
                                    Long categoryId){
        return new ProductRequestDTO(name,description,price,stockQuantity,categoryId);
    }

}
