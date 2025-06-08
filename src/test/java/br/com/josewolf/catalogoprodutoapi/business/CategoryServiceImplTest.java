package br.com.josewolf.catalogoprodutoapi.business;


import br.com.josewolf.catalogoprodutoapi.business.converter.CategoryConverter;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.CategoryRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTO;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.exceptions.ResourceNotFoundException;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryConverter categoryConverter;

    @InjectMocks
    private CategoryServiceImp categoryServiceImp;

    @Test
    @DisplayName("Deve criar uma categoria com sucesso")
    void createCategory_quandoDadosValidos_deveRetornarCategoryResponseDTO(){
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Eletrônicos");

        Category categoryEntity = new Category();
        categoryEntity.setName(requestDTO.getName());

        Category savedCategoryEntity = new Category();
        savedCategoryEntity.setId(1L);
        savedCategoryEntity.setName(requestDTO.getName());

        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO(1L,"Eletrônicos");

        when(categoryConverter.toCategoryEntity(requestDTO)).thenReturn(categoryEntity);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategoryEntity);
        when(categoryConverter.toCategory(savedCategoryEntity)).thenReturn(categoryResponseDTO);

        CategoryResponseDTO actualResponseDTO = categoryServiceImp.createCategory(requestDTO);

        assertNotNull(actualResponseDTO);
        assertEquals(categoryResponseDTO.getId(), actualResponseDTO.getId());
        assertEquals(categoryResponseDTO.getName(), actualResponseDTO.getName());

        verify(categoryConverter, times(1)).toCategoryEntity(requestDTO);
        verify(categoryRepository, times(1)).save(categoryEntity);
        verify(categoryConverter, times(1)).toCategory(savedCategoryEntity);
    }

    @Test
    @DisplayName("Buscar categoria pelo ID")
    void buscar_category_por_id_existente(){
        Long existenteId = 1L;

        Category categoryEntity = new Category();
        categoryEntity.setId(existenteId);
        categoryEntity.setName("Eletrônicos");

        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO(1l, "Eletrônicos");

        when(categoryRepository.findById(existenteId)).thenReturn(Optional.of(categoryEntity));
        when(categoryConverter.toCategory(categoryEntity)).thenReturn(categoryResponseDTO);

        Optional<CategoryResponseDTO> actualResponseOptional = categoryServiceImp.getCategoryById(existenteId);

        assertTrue(actualResponseOptional.isPresent(), "O Optional não deveria estar vazio");
        CategoryResponseDTO actualResponseDTO = actualResponseOptional.get();
        assertEquals(categoryResponseDTO.getId(), actualResponseDTO.getId());
        assertEquals(categoryResponseDTO.getName(), actualResponseDTO.getName());

        verify(categoryRepository, times(1)).findById(existenteId);
        verify(categoryConverter, times(1)).toCategory(categoryEntity);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando ID inexistente for fornecido")
    void buscar_categoryById_inexistente(){
        Long inexistenteId = 99L;

        when(categoryRepository.findById(inexistenteId)).thenReturn(Optional.empty());

        Optional<CategoryResponseDTO> actualResponseOptional = categoryServiceImp.getCategoryById(inexistenteId);

        assertFalse(actualResponseOptional.isPresent(),"O optional deveria estar vazio");

        verify(categoryRepository, times(1)).findById(inexistenteId);
        verify(categoryConverter, never()).toCategory(any(Category.class));
    }

    @Test
    @DisplayName("Deve retornar uma lista")
    void getAllCategories_quandoRepositorioRetorna() {
        Category categoryEntity = new Category();
        categoryEntity.setId(1L);
        categoryEntity.setName("Eletrônicos");

        Category categoryEntity2 = new Category();
        categoryEntity2.setId(2L);
        categoryEntity2.setName("Livros");

        List<Category> entityList = List.of(categoryEntity,categoryEntity2);

        CategoryResponseDTO dto1 = new CategoryResponseDTO(1L, "Eletrônicos");
        CategoryResponseDTO dto2 = new CategoryResponseDTO(2L, "Livros");
        List<CategoryResponseDTO> categoryResponseDTOList = List.of(dto1, dto2);

        when(categoryRepository.findAll()).thenReturn(entityList);
        when(categoryConverter.toListCategoryDTO(entityList)).thenReturn(categoryResponseDTOList);

        List<CategoryResponseDTO> actualResponseList = categoryServiceImp.getAllCategories();

        assertNotNull(actualResponseList, "A lista de resposta não deve ser nula.");
        assertEquals(2, actualResponseList.size(), "A lista deve conter uma categoria.");

        assertEquals(categoryResponseDTOList.get(0).getId(), actualResponseList.get(0).getId());
        assertEquals(categoryResponseDTOList.get(0).getName(), actualResponseList.get(0).getName());
        assertEquals(categoryResponseDTOList.get(1).getId(), actualResponseList.get(1).getId());
        assertEquals(categoryResponseDTOList.get(1).getName(), actualResponseList.get(1).getName());

        verify(categoryRepository, times(1)).findAll();
        verify(categoryConverter, times(1)).toListCategoryDTO(entityList);
        verify(categoryConverter, never()).toCategory(any(Category.class));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando o repositório não retorna categorias")
    void getAllCategories_quandoRepositorioRetornaUmaListaVazia(){
        List<Category> emptyEntityList = Collections.emptyList();
        List<CategoryResponseDTO> emptyDtoList = Collections.emptyList();

        when(categoryRepository.findAll()).thenReturn(emptyEntityList);
        when(categoryConverter.toListCategoryDTO(emptyEntityList)).thenReturn(emptyDtoList);

        List<CategoryResponseDTO> actualResponseList = categoryServiceImp.getAllCategories();

        assertNotNull(actualResponseList);
        assertTrue(actualResponseList.isEmpty());

        verify(categoryRepository, times(1)).findAll();
        verify(categoryConverter, times(1)).toListCategoryDTO(emptyEntityList);
        verify(categoryConverter, never()).toCategory(any(Category.class));
    }

    @Test
    @DisplayName("Deve atualizar uma categoria existente e retornar o DTO atualizado")
    void updateCategory_quandoExistenteEDadosValidos(){
        Long existingId = 1L;
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Eletrônicos atualizado");

        Category existingCategoryEntity = new Category();
        existingCategoryEntity.setId(existingId);
        existingCategoryEntity.setName("Eletrônico original");

        Category updateCategoryEntityAfterSave = new Category();
        updateCategoryEntityAfterSave.setId(existingId);
        updateCategoryEntityAfterSave.setName(requestDTO.getName());

        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO(existingId,"Eletrônicos Atualizado");

        when(categoryRepository.findById(existingId)).thenReturn(Optional.of(existingCategoryEntity));
        when(categoryRepository.save(existingCategoryEntity)).thenReturn(updateCategoryEntityAfterSave);
        when(categoryConverter.toCategory(updateCategoryEntityAfterSave)).thenReturn(categoryResponseDTO);

        CategoryResponseDTO actualResponseDTO = categoryServiceImp.updateCategory(existingId,requestDTO);

        assertNotNull(actualResponseDTO);
        assertEquals(categoryResponseDTO.getId(), actualResponseDTO.getId());
        assertEquals(categoryResponseDTO.getName(), actualResponseDTO.getName());

        verify(categoryRepository,times(1)).findById(existingId);
        verify(categoryConverter,times(1)).updateCategory(eq(requestDTO), eq(existingCategoryEntity));
        verify(categoryRepository, times(1)).save(existingCategoryEntity);
        verify(categoryConverter,times(1)).toCategory(updateCategoryEntityAfterSave);

    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar categoria com ID inexistente")
    void updateCategory_quandoIdInexistente(){
        Long nonExistentId  = 99L;
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Eletrônicos");

        when(categoryRepository.findById(nonExistentId )).thenReturn(Optional.empty());

        ResourceNotFoundException thrownException  = assertThrows(ResourceNotFoundException.class,
                () -> {categoryServiceImp.updateCategory(nonExistentId , requestDTO);
        });

        assertEquals("Categoria não encontrada com o id: " + nonExistentId , thrownException .getMessage());

        verify(categoryRepository, times(1)).findById(nonExistentId); // findById foi chamado
        verify(categoryConverter, never()).updateCategory(any(CategoryRequestDTO.class), any(Category.class));
        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryConverter, never()).toCategory(any(Category.class));
    }

    @Test
    @DisplayName("Deve deletar a categoria se o Id for existente")
    void deleteCategory_seIdForExistente(){
        Long existentId = 1L;

        when(categoryRepository.existsById(existentId)).thenReturn(true);
        //doNothing().when(categoryRepository.findById(existentId));

        categoryServiceImp.deleteCategory(existentId);

        verify(categoryRepository,times(1)).existsById(existentId);
        verify(categoryRepository,times(1)).deleteById(existentId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar categoria com ID inexistente")
    void deleteCategory_seIdForInexistente(){
        Long nonExistentId = 99L;

        when(categoryRepository.existsById(nonExistentId)).thenReturn(false);
        ResourceNotFoundException thrownException  = assertThrows(ResourceNotFoundException.class,
            () -> {categoryServiceImp.deleteCategory(nonExistentId);},
                "ResourceNotFoundException deveria ser lançada");


        assertNotNull(thrownException .getMessage(), "A mensagem da exceção não deve ser nula.");
        assertEquals("Categoria não encontrada com o ID: " + nonExistentId, thrownException.getMessage());

        verify(categoryRepository,times(1)).existsById(nonExistentId);
        verify(categoryRepository, never()).deleteById(nonExistentId);
    }
}
