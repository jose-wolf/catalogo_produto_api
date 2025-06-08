package br.com.josewolf.catalogoprodutoapi.business.dto.request;

public class CategoryRequestDTOFixture {

    public static CategoryRequestDTO build(String name){
        return new CategoryRequestDTO(name);
    }

}
