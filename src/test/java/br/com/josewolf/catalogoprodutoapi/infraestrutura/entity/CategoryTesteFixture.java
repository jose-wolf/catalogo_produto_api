package br.com.josewolf.catalogoprodutoapi.infraestrutura.entity;


import java.util.Collections;

public class CategoryTesteFixture {

    public static Category build(Long id,
                           String name){
        return Category.builder()
                .id(id)
                .name(name)
                .build();
    }
}
