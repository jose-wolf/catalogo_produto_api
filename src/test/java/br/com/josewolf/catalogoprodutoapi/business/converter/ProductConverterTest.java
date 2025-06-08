package br.com.josewolf.catalogoprodutoapi.business.converter;

import br.com.josewolf.catalogoprodutoapi.business.dto.request.ProductRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.ProductRequestDTOFixture;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTOFixture;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.ProductResponseDTO;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.CategoryTesteFixture;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Product;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.ProductTesteFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductConverterTest {

    @Mock
    private CategoryConverter categoryConverter;

    @InjectMocks
    private ProductConverter productConverter;


    @Test
    @DisplayName("Deve converter Product (entidade) para ProductResponseDTO corretamente")
    void toResponseDTO_deveConverterProduct() {
        CategoryResponseDTO categoriaDTOMockada = CategoryResponseDTOFixture.build(1L, "Eletrônicos Mock");
        Category categoriaEntidade = CategoryTesteFixture.build(1L, "Eletrônicos Mock");

        Product produtoEntidade = ProductTesteFixture.build(
                10L, "Notebook Teste", "Desc Notebook", new BigDecimal("5000.00"), 10
                , categoriaEntidade
        );
        when(categoryConverter.toCategory(categoriaEntidade)).thenReturn(categoriaDTOMockada);

        ProductResponseDTO actualResponseDTO = productConverter.toResponseDTO(produtoEntidade);

        assertNotNull(actualResponseDTO);
        assertEquals(produtoEntidade.getId(), actualResponseDTO.getId());
        assertEquals(produtoEntidade.getName(), actualResponseDTO.getName());
        assertEquals(produtoEntidade.getPrice(), actualResponseDTO.getPrice());
        assertEquals(produtoEntidade.getStockQuantity(), actualResponseDTO.getStockQuantity());
        assertEquals(produtoEntidade.getDescription(), actualResponseDTO.getDescription());
        assertNotNull(actualResponseDTO.getCategory());
        assertEquals(categoriaDTOMockada.getId(), actualResponseDTO.getCategory().getId());
        assertEquals(categoriaDTOMockada.getName(), actualResponseDTO.getCategory().getName());

        verify(categoryConverter, times(1)).toCategory(categoriaEntidade);
    }

    @Test
    @DisplayName("Deve retornar null ao converter ProductRequestDTO nulo para Product (entidade)")
    void toResponseDTO_deveRetornarNuloAotentarConverterProductEntityNulo() {
        Product productEntidadeNula = null;

        ProductResponseDTO actualResponseDTO = productConverter.toResponseDTO(productEntidadeNula);
        assertNull(actualResponseDTO);
        verifyNoInteractions(categoryConverter);
    }

    @Test
    @DisplayName("Deve converter ProductRequestDTO para Product (entidade) corretamente")
    void toEntity_deveConverterProductRequestDtoParaProduct() {
        Category categoriaEntidade = CategoryTesteFixture.build(1L, "Eletrônicos Mock");

        ProductRequestDTO requestDTO = ProductRequestDTOFixture.build(
                "Notebook Teste", "Desc Notebook", new BigDecimal("5000.00"), 10
                , categoriaEntidade.getId()
        );

        Product actualEntity = productConverter.toEntity(requestDTO, categoriaEntidade);

        assertNotNull(actualEntity);

        assertEquals(requestDTO.getName(), actualEntity.getName());
        assertEquals(requestDTO.getDescription(), actualEntity.getDescription());
        assertEquals(requestDTO.getPrice(), actualEntity.getPrice());
        assertEquals(requestDTO.getStockQuantity(), actualEntity.getStockQuantity());
        assertNotNull(actualEntity.getCategory());
        assertEquals(categoriaEntidade.getId(), actualEntity.getCategory().getId());
        assertEquals(categoriaEntidade.getName(), actualEntity.getCategory().getName());
    }

    @Test
    @DisplayName("Deve retornar null ao converter ProductRequestDTO nulo para Product (entidade)")
    void toEntity_deveRetornarNullParaProductEntity_quandoRequestDTOForNulo() {
        ProductRequestDTO requestDTO = null;
        Category categoriaEntidade = CategoryTesteFixture.build(1L, "Eletrônicos Mock");

        Product actualEntity = productConverter.toEntity(requestDTO, categoriaEntidade);

        assertNull(actualEntity);
    }

    @Test
    @DisplayName("Deve retornar lista vazia para toResponseDTOList se a lista de entrada for nula")
    void deveRetornarListaVazia_quandoEntradaDeToResponseDTOListForNula() {
        List<Product> productListVazia = null;

        List<ProductResponseDTO> result = productConverter.toResponseDTOList(productListVazia);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(categoryConverter);
    }

    @Test
    @DisplayName("Deve converter lista de Product para lista de ProductResponseDTO")
    void deveConverterListaDeProductParaListaDeResponseDTO(){
        CategoryResponseDTO categoriaDTO = CategoryResponseDTOFixture.build(1L, "Periféricos");
        Category categoriaEntidade = CategoryTesteFixture.build(1L, "Periféricos");
        Product produtoEntidade1 = ProductTesteFixture.build(
                20L, "Mouse Antigo", "Mouse com fio", new BigDecimal("50.00")
                , 5, categoriaEntidade
        );

        CategoryResponseDTO categoriaDTO2 = CategoryResponseDTOFixture.build(2L, "Eletronicos");
        Category categoriaEntidade2 = CategoryTesteFixture.build(2L, "Eletronicos");
        Product produtoEntidade2 = ProductTesteFixture.build(
                20L, "Monitor", "Monitor", new BigDecimal("950.00")
                , 5, categoriaEntidade2
        );

        List<Product> productList = List.of(produtoEntidade1,produtoEntidade2);

        when(categoryConverter.toCategory(categoriaEntidade)).thenReturn(categoriaDTO);
        when(categoryConverter.toCategory(categoriaEntidade2)).thenReturn(categoriaDTO2);

        List<ProductResponseDTO> result = productConverter.toResponseDTOList(productList);

        assertNotNull(result);
        assertEquals(2,result.size());

        assertEquals(produtoEntidade1.getId(), result.get(0).getId());
        assertEquals(categoriaEntidade.getId(), result.get(0).getCategory().getId());

        assertEquals(produtoEntidade2.getId(), result.get(1).getId());
        assertEquals(categoriaEntidade2.getId(), result.get(1).getCategory().getId());

        verify(categoryConverter,times(1)).toCategory(categoriaEntidade);
        verify(categoryConverter,times(1)).toCategory(categoriaEntidade2);
    }

    @Test
    @DisplayName("Deve atualizar campos da entidade Product a partir de ProductRequestDTO")
    void updateEntityFromRequestDTO_deveAtualizarCamposProductEntityComProductRequestDTO() {
        Category categoriaEntidade = CategoryTesteFixture.build(1L, "Periféricos");

        Product entidadeParaAtualizar = ProductTesteFixture.build(
                20L, "Mouse Antigo", "Mouse com fio", new BigDecimal("50.00")
                , 5, categoriaEntidade
        );

        ProductRequestDTO requestDTO = ProductRequestDTOFixture.build(
                "Mouse Novo", "Mouse sem fio", new BigDecimal("80.00"), 10
                , categoriaEntidade.getId()
        );

        productConverter.updateEntityFromRequestDTO(requestDTO, entidadeParaAtualizar, categoriaEntidade);

        assertNotNull(entidadeParaAtualizar);
        assertEquals(requestDTO.getName(), entidadeParaAtualizar.getName());
        assertEquals(requestDTO.getDescription(), entidadeParaAtualizar.getDescription());
        assertEquals(requestDTO.getPrice(), entidadeParaAtualizar.getPrice());
        assertEquals(requestDTO.getStockQuantity(), entidadeParaAtualizar.getStockQuantity());
        assertEquals(requestDTO.getCategoryId(), entidadeParaAtualizar.getCategory().getId());
    }

    @Test
    @DisplayName("Deve atualizar a categoria da entidade se uma nova categoria válida for passada")
    void deveAtualizarCategoriaEmUpdateEntity_quandoNovaCategoriaValidaForPassada() {
        Category categoriaEntidade = CategoryTesteFixture.build(1L, "Eletrônicos Mock");

        Product entidadeParaAtualizar = ProductTesteFixture.build(
                20L, "Mouse Antigo", "Mouse com fio", new BigDecimal("50.00")
                , 5, categoriaEntidade
        );

        Category novaCategoria = CategoryTesteFixture.build(2L, "Periféricos");

        ProductRequestDTO requestDTO = ProductRequestDTOFixture.build(
                "Mouse novo!",
                "Mouse com fio/sem fio",
                new BigDecimal("150.00"),
                entidadeParaAtualizar.getStockQuantity(),
                novaCategoria.getId()
        );

        productConverter.updateEntityFromRequestDTO(requestDTO,entidadeParaAtualizar,novaCategoria);

        assertNotNull(entidadeParaAtualizar.getCategory());
        assertEquals(requestDTO.getName(), entidadeParaAtualizar.getName());
        assertEquals(novaCategoria.getId(), entidadeParaAtualizar.getCategory().getId(), "Categoria deve ser atualizada para a nova.");
        assertEquals(novaCategoria.getName(), entidadeParaAtualizar.getCategory().getName());
    }

    @Test
    @DisplayName("Não deve atualizar a categoria em updateEntity se a categoria passada for nula")
    void naoDeveAtualizarCategoriaEmUpdateEntity_quandoCategoriaPassadaForNula(){
        Category categoriaEntidade = CategoryTesteFixture.build(1L, "Eletrônicos Mock");

        Product entidadeParaAtualizar = ProductTesteFixture.build(
                20L, "Mouse Antigo", "Mouse com fio", new BigDecimal("50.00")
                , 5, categoriaEntidade
        );

        ProductRequestDTO requestDTO = ProductRequestDTOFixture.build(
                "Mouse novo!",
                "Mouse com fio/sem fio",
                new BigDecimal("150.00"),
                entidadeParaAtualizar.getStockQuantity(),
                categoriaEntidade.getId()
        );

        Category categoriaEntidadeNula = null;

        productConverter.updateEntityFromRequestDTO(requestDTO,entidadeParaAtualizar,categoriaEntidadeNula);

        assertNotNull(entidadeParaAtualizar.getCategory());
        assertEquals(requestDTO.getName(), entidadeParaAtualizar.getName());
        assertEquals(categoriaEntidade.getId(), entidadeParaAtualizar.getCategory().getId(),
                "Categoria não deve mudar se a categoria passada for nula.");
    }
}

