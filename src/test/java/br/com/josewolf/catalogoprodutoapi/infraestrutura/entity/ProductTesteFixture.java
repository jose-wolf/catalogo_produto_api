package br.com.josewolf.catalogoprodutoapi.infraestrutura.entity;

import java.math.BigDecimal;

public class ProductTesteFixture {

    public static Product build(Long id,
                          String name,
                          String description,
                          BigDecimal price,
                          Integer stockQuantity,
                          Category category){
        return new Product(id,name,description,price,stockQuantity,category);
    }

}
