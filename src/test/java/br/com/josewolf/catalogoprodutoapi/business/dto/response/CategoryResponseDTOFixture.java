package br.com.josewolf.catalogoprodutoapi.business.dto.response;

public class CategoryResponseDTOFixture {

    public static CategoryResponseDTO build(Long id, String name){
        return new CategoryResponseDTO(id,name);
    }

}
