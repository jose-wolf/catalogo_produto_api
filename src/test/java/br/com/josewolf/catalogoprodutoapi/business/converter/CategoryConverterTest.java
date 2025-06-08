package br.com.josewolf.catalogoprodutoapi.business.converter;

import br.com.josewolf.catalogoprodutoapi.business.dto.request.CategoryRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.CategoryRequestDTOFixture;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTO;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.CategoryTesteFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CategoryConverterTest {

    @InjectMocks
    private CategoryConverter categoryConverter;

    @Test
    @DisplayName("Deve converter Category (entidade) para CategoryResponseDTO corretamente")
    void toCategory_deveConverterCategoryParaCategoryResponseDto(){
        Category categoryEntidade = CategoryTesteFixture.build(1L, "Periféricos");
        assertNotNull(categoryEntidade, "Falha no setup: categoryEntidade não deveria ser nula.");

        CategoryResponseDTO responseDTO = categoryConverter.toCategory(categoryEntidade);

        assertNotNull(responseDTO, "O DTO de resposta não deve ser nulo após a conversão.");
        assertEquals(responseDTO.getId(),categoryEntidade.getId());
        assertEquals(responseDTO.getName(),categoryEntidade.getName());
    }

    @Test
    @DisplayName("Deve converter CategoryRequestDTO para Category (entidade) corretamente")
    void toCategoryEntity_deveConverterRequestDTOParaEntity() {
        CategoryRequestDTO requestDTO = CategoryRequestDTOFixture.build("Periféricos");
        assertNotNull(requestDTO, "CategoryRequestDTO não pode ser nulo");

        Category categoryEntidade = categoryConverter.toCategoryEntity(requestDTO);

        assertNotNull(categoryEntidade);
        assertEquals(requestDTO.getName(), categoryEntidade.getName(), "O nome da entidade deve ser o do DTO");
        assertNull(categoryEntidade.getId(), "O ID da nova entidade Category deve ser nulo antes de persistir.");
        assertTrue(categoryEntidade.getProducts() == null || categoryEntidade.getProducts().isEmpty(),
                "A lista de produtos da nova entidade Category deve ser nula ou vazia.");
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para toCategoryEntity se CategoryRequestDTO for nulo")
    void toCategoryEntity_deveLancarIllegalArgumentException_quandoRequestDTONulo() {
        CategoryRequestDTO requestDTONulo = null;

        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
            categoryConverter.toCategoryEntity(requestDTONulo);
        }, "Deveria lançar NullPointerException se o DTO de requisição for nulo.");

        assertEquals("Argumentos inválidos", thrownException.getMessage());
    }

    @Test
    @DisplayName("Deve converter lista de CategoryRequestDTO para lista de Category (entidade)")
    void toCategoryEntityList_deveConverterListaRequestDTOParaListaEntity() {
        CategoryRequestDTO requestDTO1 = CategoryRequestDTOFixture.build("Periféricos");
        CategoryRequestDTO requestDTO2 = CategoryRequestDTOFixture.build("Eletrônicos");

        List<CategoryRequestDTO> result = List.of(requestDTO1,requestDTO2);

        List<Category> categoryEntidadeList = categoryConverter.toCategoryEntityList(result);
        assertNotNull(categoryEntidadeList, "A lista de entidades não deve ser nula.");
        assertEquals(2, categoryEntidadeList.size(), "O tamanho da lista de entidades deve ser 2.");
        assertEquals(requestDTO1.getName(), categoryEntidadeList.get(0).getName());
        assertEquals(requestDTO2.getName(), categoryEntidadeList.get(1).getName());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para toCategoryEntityList se a lista de DTOs for nula")
    void toCategoryEntityList_deveRetornarListaVazia_quandoListaRequestDTONula() {
        List<CategoryRequestDTO> result = null;

        List<Category> categoriaEntidadeList = categoryConverter.toCategoryEntityList(result);

        assertNotNull(categoriaEntidadeList, "A lista de entidades não deve ser nula (deve ser vazia).");
        assertTrue(categoriaEntidadeList.isEmpty(), "A lista de entidades deve estar vazia.");
    }

    @Test
    @DisplayName("Deve converter lista de Category (entidade) para lista de CategoryResponseDTO")
    void toListCategoryDTO_deveConverterListaEntityParaListaResponseDTO() {
        Category categoyEntidade = CategoryTesteFixture.build(1L, "Smartphone");
        Category categoyEntidade2 = CategoryTesteFixture.build(2L, "Tablets");
        List<Category> categoryEntityList = List.of(categoyEntidade, categoyEntidade2);


        List<CategoryResponseDTO> result = categoryConverter.toListCategoryDTO(categoryEntityList);
        assertNotNull(result, "A lista de DTOs de resposta não deve ser nula.");
        assertEquals(2, result.size(), "O tamanho da lista de DTOs deve ser 2.");
        assertEquals(categoyEntidade.getId(), result.get(0).getId());
        assertEquals(categoyEntidade.getName(), result.get(0).getName());
        assertEquals(categoyEntidade2.getId(), result.get(1).getId());
        assertEquals(categoyEntidade2.getName(), result.get(1).getName());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para toListCategoryDTO se a lista de entidades for nula")
    void toListCategoryDTO_deveRetornarListaVazia_quandoListaEntidadesNula() {
        List<Category> categoryEntityListNula = null;

        List<CategoryResponseDTO> responseDTOList = categoryConverter.toListCategoryDTO(categoryEntityListNula);
        assertNotNull(responseDTOList, "A lista de DTOs não deve ser nula (deve ser vazia).");
        assertTrue(responseDTOList.isEmpty(), "A lista de DTOs deve estar vazia.");
    }


    @Test
    @DisplayName("Deve retornar lista vazia para toListCategoryDTO se a lista de entidades for vazia")
    void toListCategoryDTO_deveRetornarListaVazia_quandoListaEntidadesVazia(){
        List<Category> categoryEntityListVazia = Collections.emptyList();

        List<CategoryResponseDTO> responseDTOList = categoryConverter.toListCategoryDTO(categoryEntityListVazia);
        assertNotNull(responseDTOList, "A lista de DTOs não deve ser nula.");
        assertTrue(responseDTOList.isEmpty(), "A lista de DTOs deve estar vazia.");
    }

    @Test
    @DisplayName("Deve atualizar o nome da Category (entidade) a partir do CategoryRequestDTO")
    void updateCategory_deveAtualizarNomeDaEntidade() {
        Category categoryEntidade = CategoryTesteFixture.build(1L,"Eletrônicos móveis");

        String nomeAtualizado = "Smartphone";
        CategoryRequestDTO result = CategoryRequestDTOFixture.build(nomeAtualizado);

        categoryConverter.updateCategory(result, categoryEntidade);

        assertEquals(nomeAtualizado, categoryEntidade.getName(), "O nome da entidade deve ser atualizado.");
        assertEquals(1L, categoryEntidade.getId(), "O ID da entidade não deve mudar.");
    }

    @Test
    @DisplayName("Não deve alterar o nome em updateCategory se o nome no DTO for nulo")
    void updateCategory_naoDeveAlterarNome_seNomeNoDTONulo() {
        String nomeOriginal = "Eletrônicos móveis";
        Category categoryEntidade = CategoryTesteFixture.build(1L,"Eletrônicos móveis");

        CategoryRequestDTO requestDTOComNomeNulo = CategoryRequestDTOFixture.build(null);

        categoryConverter.updateCategory(requestDTOComNomeNulo, categoryEntidade);
        assertEquals(nomeOriginal, categoryEntidade.getName(), "O nome da entidade não deve mudar se o nome no DTO for nulo.");

    }

    @Test
    @DisplayName("Não deve fazer nada em updateCategory se CategoryRequestDTO for nulo")
    void updateCategory_naoDeveFazerNada_seRequestDTONulo() {
        Category categoryEntidade = CategoryTesteFixture.build(1L,"Eletrônicos móveis");

        CategoryRequestDTO requestDTONulo = null;

        assertDoesNotThrow(() -> {
            categoryConverter.updateCategory(requestDTONulo, categoryEntidade);
        });
        assertEquals(categoryEntidade.getName(), categoryEntidade.getName(), "O nome da entidade não deve mudar.");
        assertEquals(categoryEntidade.getId(), categoryEntidade.getId(), "O ID da entidade não deve mudar.");
    }

    @Test
    @DisplayName("Não deve fazer nada em updateCategory se Category (entidade) for nula")
    void updateCategory_naoDeveFazerNada_seEntidadeNula() {
        Category categoryEntityNula = null;
        CategoryRequestDTO requestDTO = CategoryRequestDTOFixture.build("Qualquer Nome");

        assertDoesNotThrow(() -> {
            categoryConverter.updateCategory(requestDTO, categoryEntityNula);
        });
    }
}
