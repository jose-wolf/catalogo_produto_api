package br.com.josewolf.catalogoprodutoapi.controller;

import br.com.josewolf.catalogoprodutoapi.business.ProductService;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.ProductRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.ProductRequestDTOFixture;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.ProductResponseDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.ProductResponseDTOFixture;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductController productController;

    private CategoryResponseDTO categoryResponseDTO;
    private ProductResponseDTO productResponseDTO1;
    private ProductResponseDTO productResponseDTO2;
    private ProductRequestDTO productRequestDTO;
    private List<ProductResponseDTO> productList;
    private Long existingProductId;
    private Long nonExistentProductId;
    private Long categoryIdForProduct;
    private String url;
    private String url_id;

    @BeforeEach
    void setUp(){
        url = "/api/v1/products";
        url_id = "/api/v1/products/{id}";

        existingProductId = 1L;
        nonExistentProductId = 99L;
        categoryIdForProduct = 1L;

        categoryResponseDTO = new CategoryResponseDTO(categoryIdForProduct, "Eletrônicos");

        productRequestDTO = ProductRequestDTOFixture.build(
                "Notebook Gamer X",
                "Notebook de alta performance",
                new BigDecimal("7500.00"),
                10,
                categoryIdForProduct
        );

        productResponseDTO1 = ProductResponseDTOFixture.build(
                existingProductId,
                "Notebook Gamer X",
                "Notebook de alta performance",
                new BigDecimal("7500.00"),
                10,
                categoryResponseDTO
        );

        productResponseDTO2 = ProductResponseDTOFixture.build(
                2L,
                "Mouse Sem Fio",
                "Mouse ergonômico",
                new BigDecimal("150.00"),
                50,
                categoryResponseDTO
        );

        productList = new ArrayList<>();
        productList.add(productResponseDTO1);
        productList.add(productResponseDTO2);
    }

    @Test
    @DisplayName("POST /products - Deve criar produto e retornar status 201 Created")
    void createProduct_deveCriarProduto_eStatusCreated() throws Exception {
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(productResponseDTO1);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(productResponseDTO1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(productResponseDTO1.getName())))
                .andExpect(jsonPath("$.category.id", is(productResponseDTO1.getCategory().getId().intValue())));

        verify(productService,times(1)).createProduct(any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("POST /products - Deve retornar status 400 Bad Request para DTO inválido")
    void createProduct_deveRetornarStatusBadRequest_paraDTOInvalido() throws Exception {
        ProductRequestDTO invalidRequestDTO = ProductRequestDTOFixture.build(
                "",
                "Descrição",
                new BigDecimal("775.20"),
                10,
                categoryIdForProduct
        );

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(invalidRequestDTO);
    }

    @Test
    @DisplayName("GET /products - Deve retornar lista de todos os produtos com status 200 OK")
    void getAllProducts_deveRetornarListaDeProdutos_eStatusOK() throws Exception {
        when(productService.getAllProducts()).thenReturn(productList);

        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(productList.size())))
                .andExpect(jsonPath("$[0].id", is(productResponseDTO1.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(productResponseDTO1.getName())))
                .andExpect(jsonPath("$[0].category.name", is(productResponseDTO1.getCategory().getName())))
                .andExpect(jsonPath("$[1].id", is(productResponseDTO2.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(productResponseDTO2.getName())));

        verify(productService,times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /products - Deve retornar lista vazia com status 200 OK se não houver produtos")
    void getAllProducts_deveRetornarListaVazia_eStatusOK_quandoNaoExistiremProdutos() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(0)));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /products/{id} - Deve retornar produto e status 200 OK quando ID existir")
    void getProductById_deveRetornarProduto_eStatusOK_quandoIdExistir() throws Exception {
        when(productService.getProductById(existingProductId)).thenReturn(Optional.of(productResponseDTO1));

        mockMvc.perform(get(url_id,existingProductId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(productResponseDTO1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(productResponseDTO1.getName())))
                .andExpect(jsonPath("$.category.id", is(productResponseDTO1.getCategory().getId().intValue())));

        verify(productService,times(1)).getProductById(existingProductId);
    }

    @Test
    @DisplayName("GET /products/{id} - Deve retornar status 404 Not Found quando ID não existir")
    void getProductById_deveRetornarStatus404_quandoIdNaoExistir() throws Exception {
        when(productService.getProductById(nonExistentProductId)).thenReturn(Optional.empty());

        mockMvc.perform(get(url_id, nonExistentProductId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService,times(1)).getProductById(nonExistentProductId);
    }

    @Test
    @DisplayName("PUT /products/{id} - Deve atualizar produto e retornar status 200 OK")
    void updateProduct_deveAtualizarProduto_eStatusOK() throws Exception {
        ProductRequestDTO updateDetailsDTO = ProductRequestDTOFixture.build(
                "Notebook Gamer X Ultra",
                productRequestDTO.getDescription(),
                productRequestDTO.getPrice(),
                productRequestDTO.getStockQuantity(),
                productRequestDTO.getCategoryId()
        );

        ProductResponseDTO updatedResponseDTO = ProductResponseDTOFixture.build(
                existingProductId,
                updateDetailsDTO.getName(),
                updateDetailsDTO.getDescription(),
                updateDetailsDTO.getPrice(),
                updateDetailsDTO.getStockQuantity(),
                categoryResponseDTO
        );

        when(productService.updateProduct(eq(existingProductId), any(ProductRequestDTO.class)))
                .thenReturn(updatedResponseDTO);

        mockMvc.perform(put(url_id,existingProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetailsDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(updatedResponseDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(updatedResponseDTO.getName())));

        verify(productService,times(1)).updateProduct(eq(existingProductId),
                any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /products/{id} - Deve retornar status 404 Not Found quando ID não existir para atualizar")
    void updateProduct_deveRetornarStatus404_quandoIdNaoExistir() throws Exception {
        ProductRequestDTO updateDetailsDTO = ProductRequestDTOFixture.build(
                "Qualquer Nome",
                "Qualquer Desc",
                new BigDecimal("20.00"),
                1,
                categoryIdForProduct
        );

        when(productService.updateProduct(eq(nonExistentProductId), any(ProductRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Produto não encontrado com ID: " + nonExistentProductId));

        mockMvc.perform(put(url_id,nonExistentProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetailsDTO)))
                .andExpect(status().isNotFound());

        verify(productService,times(1)).updateProduct(eq(nonExistentProductId),
                any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /products/{id} - Deve retornar status 400 Bad Request para DTO de atualização inválido")
    void updateProduct_deveRetornarStatusBadRequest_paraDTOInvalido() throws Exception {
        ProductRequestDTO invalidRequestDTO = ProductRequestDTOFixture.build(
                "",
                "Qualquer Desc",
                new BigDecimal("20.00"),
                1,
                categoryIdForProduct
        );

        mockMvc.perform(put(url_id,existingProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());
        verify(productService, never()).updateProduct(anyLong(), any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /products/{id} - Deve excluir produto e retornar status 204 No Content")
    void deleteProduct_deveExcluirProduto_eStatusNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(existingProductId);

        mockMvc.perform(delete(url_id,existingProductId))
                .andExpect(status().isNoContent());

        verify(productService,times(1)).deleteProduct(existingProductId);
    }

    @Test
    @DisplayName("DELETE /products/{id} - Deve retornar status 404 Not Found quando ID não existir para deletar")
    void deleteProduct_deveRetornarStatus404_quandoIdNaoExistir() throws Exception {
        doThrow(new ResourceNotFoundException("Produto não encontrado com ID: " + nonExistentProductId))
                .when(productService).deleteProduct(nonExistentProductId);

        mockMvc.perform(delete("/api/v1/products/{id}", nonExistentProductId))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct(nonExistentProductId);
    }
}
