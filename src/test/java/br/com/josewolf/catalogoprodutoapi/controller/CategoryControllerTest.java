package br.com.josewolf.catalogoprodutoapi.controller;

import br.com.josewolf.catalogoprodutoapi.business.CategoryService;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.CategoryRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.CategoryRequestDTOFixture;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTOFixture;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryResponseDTO categoryResponseDTO1;
    private CategoryResponseDTO categoryResponseDTO2;
    private CategoryRequestDTO categoryRequestDTO;
    private List<CategoryResponseDTO> categoryList;
    private Long existingCategoryId;
    private Long nonExistingCategoryId;
    private String url;
    private String url_id;

    @BeforeEach
    void setUp() {
        url = "/api/v1/categories";
        url_id = "/api/v1/categories/{id}";

        existingCategoryId = 1L;
        nonExistingCategoryId = 99L;

        categoryRequestDTO = CategoryRequestDTOFixture.build("Eletrônicos");
        categoryResponseDTO1 = CategoryResponseDTOFixture.build(1L, "Eletrônicos");
        categoryResponseDTO2 = CategoryResponseDTOFixture.build(2L, "Livros");

        categoryList = new ArrayList<>();
        categoryList.add(categoryResponseDTO1);
        categoryList.add(categoryResponseDTO2);
    }

    @Test
    @DisplayName("POST /categories - Deve criar categoria e retornar status 201 Created")
    void createCategory_deveCriarCategoria_eStatusCreated() throws Exception {
        CategoryResponseDTO createdCategoryResponse = new CategoryResponseDTO(3L, categoryRequestDTO.getName()); // Simula o DTO retornado pelo serviço
        when(categoryService.createCategory(any(CategoryRequestDTO.class))).thenReturn(createdCategoryResponse);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDTO))) // Envia o DTO de requisição como JSON
                .andExpect(status().isCreated()) // Verifica o status HTTP 201 Created
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(createdCategoryResponse.getId().intValue())))
                .andExpect(jsonPath("$.name", is(createdCategoryResponse.getName())));

        verify(categoryService, times(1)).createCategory(any(CategoryRequestDTO.class));

    }

    @Test
    @DisplayName("POST /categories - Deve retornar status 400 Bad Request para DTO inválido (ex: nome em branco)")
    void createCategory_deveRetornarStatusBadRequest_paraDTOInvalido() throws Exception {
        CategoryRequestDTO requestDTOVazio = new CategoryRequestDTO("");

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTOVazio)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any(CategoryRequestDTO.class));
    }

    // --- Testes para GET /api/v1/categories (getAllCategories) ---
    @Test
    @DisplayName("GET /categories - Deve retornar lista de todas as categorias com status 200 OK")
    void getAllCategories_deveRetornarListaDeCategorias_eStatusOK() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(categoryList);

        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(categoryList.size())))
                .andExpect(jsonPath("$[0].id", is(categoryResponseDTO1.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(categoryResponseDTO1.getName())))
                .andExpect(jsonPath("$[1].id", is(categoryResponseDTO2.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(categoryResponseDTO2.getName())));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @DisplayName("GET /categories - Deve retornar lista vazia com status 200 OK se não houver categorias")
    void getAllCategories_deveRetornarListaVazia_eStatusOK_quandoNaoExistiremCategorias() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(0)));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @DisplayName("GET /categories/{id} - Deve retornar categoria e status 200 ok quando id existir")
    void getCategoryById_deveRetornarCategoria_eStatusOK_quandoIdExistir() throws Exception {
        when(categoryService.getCategoryById(existingCategoryId)).thenReturn(Optional.of(categoryResponseDTO1));

        mockMvc.perform(get(url_id, existingCategoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(categoryResponseDTO1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(categoryResponseDTO1.getName())));

        verify(categoryService, times(1)).getCategoryById(existingCategoryId);
    }

    @Test
    @DisplayName("GET /categories/{id} - Deve retornar status 404 Not Found quando ID não existir")
    void getCategoryById_deveRetornarStatus404_quandoIdNaoExistir() throws Exception {
        when(categoryService.getCategoryById(nonExistingCategoryId)).thenReturn(Optional.empty());

        mockMvc.perform(get(url_id, nonExistingCategoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).getCategoryById(nonExistingCategoryId);
    }

    @Test
    @DisplayName("PUT /categories/{id} - Deve atualizar categoria e retornar status 200 OK")
    void updateCategory_deveAtualizarCategoria_eStatusOK() throws Exception {
        CategoryRequestDTO updateDetailsDTO = new CategoryRequestDTO("Eletrônicos Atualizado");
        CategoryResponseDTO updatedResponseDTO = new CategoryResponseDTO(existingCategoryId, "Eletrônicos Atualizado");

        when(categoryService.updateCategory(eq(existingCategoryId), any(CategoryRequestDTO.class))).thenReturn(updatedResponseDTO);

        mockMvc.perform(put(url_id, existingCategoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetailsDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(updatedResponseDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(updatedResponseDTO.getName())));

        verify(categoryService, times(1)).updateCategory(eq(existingCategoryId), any(CategoryRequestDTO.class));

    }

    @Test
    @DisplayName("PUT /categories/{id} - Deve retornar status 404 Not Found quando ID não existir")
    void updateCategory_deveRetornarStatus404_quandoIdNaoExistir() throws Exception {
        CategoryRequestDTO updateDetailsDTO = new CategoryRequestDTO("Eletrônicos Atualizado");

        when(categoryService.updateCategory(eq(nonExistingCategoryId), any(CategoryRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Categoria não encontrada para atualização com ID: "
                        + nonExistingCategoryId));

        mockMvc.perform(put(url_id, nonExistingCategoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetailsDTO)))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).updateCategory(eq(nonExistingCategoryId),
                any(CategoryRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /categories/{id} - Deve retornar status 400 Bad Request para DTO inválido")
    void updateCategory_deveRetornarStatusBadRequest_paraDTOInvalido() throws Exception {
        CategoryRequestDTO invalidUpdateDetailsDTO = new CategoryRequestDTO("");

        mockMvc.perform(put(url_id, existingCategoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdateDetailsDTO)))
                .andExpect(status().isBadRequest());

        verify(categoryService,never()).updateCategory(eq(existingCategoryId), any(CategoryRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /categories/{id} - Deve excluir categoria e retornar status 204 No Content")
    void deleteCategory_deveExcluirCategoria_eStatusNoContent() throws Exception {
        doNothing().when(categoryService).deleteCategory(existingCategoryId);

        mockMvc.perform(delete(url_id,existingCategoryId))
                .andExpect(status().isNoContent());

        verify(categoryService,times(1)).deleteCategory(existingCategoryId);
    }

    @Test
    @DisplayName("DELETE /categories/{id} - Deve retornar status 404 Not Found quando ID não existir")
    void deleteCategory_deveRetornarStatus404_quandoIdNaoExistir() throws Exception{
        doThrow(new ResourceNotFoundException("Categoria não encontrada para exclusão"))
                .when(categoryService).deleteCategory(nonExistingCategoryId);

        mockMvc.perform(delete(url_id,nonExistingCategoryId))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).deleteCategory(nonExistingCategoryId);
    }
}
