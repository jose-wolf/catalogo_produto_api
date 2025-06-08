package br.com.josewolf.catalogoprodutoapi.business;

import br.com.josewolf.catalogoprodutoapi.business.converter.ProductConverter;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.ProductRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.CategoryResponseDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.ProductResponseDTO;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Product;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.exceptions.ResourceNotFoundException;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.repository.CategoryRepository;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductConverter productConverter;

    @InjectMocks
    private ProductServiceImp productServiceImp;

    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;
    private CategoryResponseDTO categoryResponseDTO;
    private Category existingCategory;
    private Product productEntityToSave;
    private Product savedProduct;
    private Long existingProductId;
    private Long existingCategoryId;
    private Long nonExistentProducttId;

    @BeforeEach
    void setup(){
        existingProductId = 1L;
        existingCategoryId = 1L;
        nonExistentProducttId = 99L;

        productRequestDTO = new ProductRequestDTO(
                "Notebook Gamer",
                "Notebook para jogos avançados",
                new BigDecimal("750.99"),
                10,
                existingCategoryId
        );

        existingCategory = new Category();
        existingCategory.setId(existingCategoryId);
        existingCategory.setName("Eletrônicos");

        productEntityToSave = new Product();
        productEntityToSave.setName(productRequestDTO.getName());
        productEntityToSave.setPrice(productRequestDTO.getPrice());
        productEntityToSave.setDescription(productRequestDTO.getDescription());
        productEntityToSave.setStockQuantity(productRequestDTO.getStockQuantity());
        productEntityToSave.setCategory(existingCategory);

        savedProduct = new Product();
        savedProduct.setId(existingProductId);
        savedProduct.setName(productRequestDTO.getName());
        savedProduct.setDescription(productRequestDTO.getDescription());
        savedProduct.setPrice(productRequestDTO.getPrice());
        savedProduct.setStockQuantity(productRequestDTO.getStockQuantity());
        savedProduct.setCategory(existingCategory);

        categoryResponseDTO = new CategoryResponseDTO(existingCategoryId, existingCategory.getName());

        productResponseDTO = new ProductResponseDTO(
                existingProductId,
                productRequestDTO.getName(),
                productRequestDTO.getDescription(),
                productRequestDTO.getPrice(),
                productRequestDTO.getStockQuantity(),
                categoryResponseDTO
        );
    }

    @Test
    @DisplayName("Deve criar um produto com sucesso quando dados válidos e categoria existente são fornecidos")
    void createProduct_quandoDadosValidosECategoriaExistente(){
        when(categoryRepository.findById(existingCategoryId)).thenReturn(Optional.of(existingCategory));
        when(productConverter.toEntity(productRequestDTO, existingCategory)).thenReturn(productEntityToSave);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productConverter.toResponseDTO(savedProduct)).thenReturn(productResponseDTO);

        ProductResponseDTO actualResponseDTO = productServiceImp.createProduct(productRequestDTO);

        assertNotNull(actualResponseDTO);
        assertEquals(productResponseDTO.getId(), actualResponseDTO.getId());
        assertEquals(productResponseDTO.getName(), actualResponseDTO.getName());
        assertEquals(productResponseDTO.getDescription(), actualResponseDTO.getDescription());
        assertEquals(productResponseDTO.getPrice(), actualResponseDTO.getPrice());
        assertEquals(productResponseDTO.getCategory(), actualResponseDTO.getCategory());

        verify(categoryRepository, times(1)).findById(existingCategoryId);
        verify(productConverter, times(1)).toEntity(productRequestDTO, existingCategory);
        verify(productRepository,times(1)).save(productEntityToSave);
        verify(productConverter, times(1)).toResponseDTO(savedProduct);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar criar produto com categoryId inexistente")
    void createProduct_quandoCategoriaInexistente(){
        ProductRequestDTO requestWithNonExistentCategory = new ProductRequestDTO(
                "Produto sem categoria",
                "Sem descrição",
                new BigDecimal("10.00"),
                5,
                nonExistentProducttId
        );

        when(categoryRepository.findById(nonExistentProducttId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> {productServiceImp.createProduct(requestWithNonExistentCategory);},
                "Deve ser lançado ResourceNotFoundException");

        assertEquals("Categoria não encontrada com o ID: " + nonExistentProducttId, exception.getMessage());

        verify(productConverter,never()).toEntity(any(), any());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    void getAllProducts_deveListarTodosOsProdutos(){
        Long productId2 = 200L;
        Product savedProductEntity2 = Product.builder()
                .id(productId2)
                .name("Mouse Gamer")
                .description("Mouse com alta precisão")
                .price(new BigDecimal("250.00"))
                .stockQuantity(50)
                .category(existingCategory) // Usando a categoria do setup
                .build();

        ProductResponseDTO productResponseDTO2 = ProductResponseDTO.builder()
                .id(productId2)
                .name("Mouse Gamer")
                .description("Mouse com alta precisão")
                .price(new BigDecimal("250.00"))
                .stockQuantity(50)
                .category(categoryResponseDTO)
                .build();

        List<Product> productList = List.of(savedProduct, savedProductEntity2);

        List<ProductResponseDTO> productResponseDTOS = List.of(productResponseDTO, productResponseDTO2);

        when(productRepository.findAll()).thenReturn(productList);
        when(productConverter.toResponseDTOList(productList)).thenReturn(productResponseDTOS);

        List<ProductResponseDTO> actualResponseDtoList = productServiceImp.getAllProducts();

        assertNotNull(actualResponseDtoList);
        assertEquals(2, actualResponseDtoList.size(), "A lista deve conter dois produtos");

        assertEquals(productResponseDTOS.get(0).getId(), actualResponseDtoList.get(0).getId());
        assertEquals(productResponseDTOS.get(0).getName(), actualResponseDtoList.get(0).getName());
        assertEquals(productResponseDTOS.get(1).getId(), actualResponseDtoList.get(1).getId());
        assertEquals(productResponseDTOS.get(1).getName(), actualResponseDtoList.get(1).getName());

        verify(productRepository, times(1)).findAll();
        verify(productConverter, times(1)).toResponseDTOList(productList);
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia caso os produtos sejam inexistentes")
    void getAllProducts_quandoRepositoRetornaUmaListaVazia(){
        List<Product> emptyList = Collections.emptyList();
        List<ProductResponseDTO> productResponseDTOS = Collections.emptyList();

        when(productRepository.findAll()).thenReturn(emptyList);
        when(productConverter.toResponseDTOList(emptyList)).thenReturn(productResponseDTOS);

        List<ProductResponseDTO> actualResponseDtoList = productServiceImp.getAllProducts();

        assertNotNull(actualResponseDtoList);
        assertTrue(actualResponseDtoList.isEmpty());

        verify(productRepository, times(1)).findAll();
        verify(productConverter, times(1)).toResponseDTOList(emptyList);
        verify(productConverter, never()).toResponseDTO(any(Product.class));
    }

    @Test
    @DisplayName("Deve retornar Optional com ProductResponseDTO quando ID existente é fornecido")
    void getProductById_deveGerarUmaListaDoProduto_seIdForValido(){
        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(productEntityToSave));
        when(productConverter.toResponseDTO(productEntityToSave)).thenReturn(productResponseDTO);

        Optional<ProductResponseDTO> actualResponseOptional = productServiceImp.getProductById(existingProductId);

        assertNotNull(actualResponseOptional);
        assertTrue(actualResponseOptional.isPresent());
        ProductResponseDTO actualResponse = actualResponseOptional.get();
        assertEquals(productResponseDTO.getId(),actualResponse.getId());
        assertEquals(productResponseDTO.getName(),actualResponse.getName());
        assertEquals(productResponseDTO.getDescription(),actualResponse.getDescription());
        assertEquals(0, productResponseDTO.getPrice().compareTo(actualResponse.getPrice()),
                "O preço do produto deve ser igual");
        assertEquals(productResponseDTO.getCategory(),actualResponse.getCategory());

        verify(productRepository,times(1)).findById(existingProductId);
        verify(productConverter, times(1)).toResponseDTO(productEntityToSave);
    }

    @Test
    @DisplayName("Deve retornar um Optinal vazio quando ID for inexistente")
    void getProductById_deveGerarUmOptionalVazioSeIdForinexistente(){
        when(productRepository.findById(nonExistentProducttId)).thenReturn(Optional.empty());

        Optional<ProductResponseDTO> actualResponseDto = productServiceImp.getProductById(nonExistentProducttId);

        assertTrue(actualResponseDto.isEmpty(), "Deve retornar o Optional vazio");

        verify(productRepository, times(1)).findById(nonExistentProducttId);
        verify(productConverter, never()).toResponseDTO(any(Product.class));
    }

    @Test
    @DisplayName("Deve atualizar o produto e sua categoria com sucesso")
    void updateProduct_deveAtualizarProdutoESuaCategoria() {
        Long productIdToUpdate = existingProductId;
        Long newCategoryId = 2L;
        String updatedName = "Notebook UltraPro";


        ProductRequestDTO updateRequestDTO = new ProductRequestDTO(
                updatedName,
                "Descrição atualizada para UltraPro",
                new BigDecimal("800.00"),
                5,
                newCategoryId
        );

        Product productEntityFetchedFromDb = new Product();
        productEntityFetchedFromDb.setId(savedProduct.getId());
        productEntityFetchedFromDb.setName(savedProduct.getName());
        productEntityFetchedFromDb.setDescription(savedProduct.getDescription());
        productEntityFetchedFromDb.setPrice(savedProduct.getPrice());
        productEntityFetchedFromDb.setStockQuantity(savedProduct.getStockQuantity());
        productEntityFetchedFromDb.setCategory(savedProduct.getCategory());


        Category newCategoryEntity = new Category();
        newCategoryEntity.setId(newCategoryId);
        newCategoryEntity.setName("Computadores de Alta Performance");


        ProductResponseDTO expectedResponseDTO = new ProductResponseDTO(
                productIdToUpdate,
                updatedName,
                updateRequestDTO.getDescription(),
                updateRequestDTO.getPrice(),
                updateRequestDTO.getStockQuantity(),
                new CategoryResponseDTO(newCategoryId, newCategoryEntity.getName())
        );


        when(productRepository.findById(productIdToUpdate)).thenReturn(Optional.of(productEntityFetchedFromDb));
        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.of(newCategoryEntity));


        doAnswer(invocation -> {
            ProductRequestDTO dtoArg = invocation.getArgument(0);
            Product productToUpdateArg = invocation.getArgument(1);
            productToUpdateArg.setName(dtoArg.getName());
            productToUpdateArg.setDescription(dtoArg.getDescription());
            productToUpdateArg.setPrice(dtoArg.getPrice());
            productToUpdateArg.setStockQuantity(dtoArg.getStockQuantity());
            return null;
        }).when(productConverter).updateEntityFromRequestDTO(
                eq(updateRequestDTO),
                eq(productEntityFetchedFromDb),
                eq(existingCategory)
        );
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(productConverter.toResponseDTO(eq(productEntityFetchedFromDb))).thenReturn(expectedResponseDTO);

        ArgumentCaptor<Product> productCaptorForSave = ArgumentCaptor.forClass(Product.class);

        ProductResponseDTO actualResponseDTO = productServiceImp.updateProduct(productIdToUpdate, updateRequestDTO);

        assertNotNull(actualResponseDTO);
        assertEquals(expectedResponseDTO.getId(), actualResponseDTO.getId());
        assertEquals(expectedResponseDTO.getName(), actualResponseDTO.getName(), "O nome no DTO de resposta deve ser o atualizado.");
        assertEquals(expectedResponseDTO.getDescription(), actualResponseDTO.getDescription());
        assertEquals(expectedResponseDTO.getPrice(), actualResponseDTO.getPrice());
        assertEquals(expectedResponseDTO.getStockQuantity(), actualResponseDTO.getStockQuantity());
        assertNotNull(actualResponseDTO.getCategory());
        assertEquals(expectedResponseDTO.getCategory().getId(), actualResponseDTO.getCategory().getId());
        assertEquals(expectedResponseDTO.getCategory().getName(), actualResponseDTO.getCategory().getName());

        verify(productRepository).findById(productIdToUpdate);
        verify(categoryRepository).findById(newCategoryId);
        verify(productConverter).updateEntityFromRequestDTO(eq(updateRequestDTO), eq(productEntityFetchedFromDb), eq(existingCategory));
        verify(productRepository).save(productCaptorForSave.capture());
        verify(productConverter).toResponseDTO(eq(productEntityFetchedFromDb));

        Product savedProductEntity = productCaptorForSave.getValue();
        assertEquals(updatedName, savedProductEntity.getName(), "O nome da entidade salva deve ser o atualizado.");
        assertEquals(updateRequestDTO.getDescription(), savedProductEntity.getDescription());
        assertEquals(updateRequestDTO.getPrice(), savedProductEntity.getPrice());
        assertEquals(updateRequestDTO.getStockQuantity(), savedProductEntity.getStockQuantity());
        assertNotNull(savedProductEntity.getCategory());
        assertEquals(newCategoryId, savedProductEntity.getCategory().getId(), "O ID da categoria da entidade salva deve ser o da nova categoria.");
    }

    @Test
    @DisplayName("Deve atualizar o produto sem mudar a categoria se o ID da categoria no DTO for o mesmo")
    void updateProduct_atualizarSemMudarCategoria_seIdDaCategoriaNoDtoForOmesmo(){
        Long productIdToUpdate = existingProductId;

        String nomeOriginalDoProdutoNesteTeste = "Produto Teste Específico V1";
        String descricaoOriginalNesteTeste = "Descrição original para V1";
        BigDecimal precoOriginalNesteTeste = new BigDecimal("300.50");
        int estoqueOriginalNesteTeste = 15;

        Product productEntityFetchedFromDb = new Product();
        productEntityFetchedFromDb.setId(productIdToUpdate);
        productEntityFetchedFromDb.setName(nomeOriginalDoProdutoNesteTeste);
        productEntityFetchedFromDb.setDescription(descricaoOriginalNesteTeste);
        productEntityFetchedFromDb.setPrice(precoOriginalNesteTeste);
        productEntityFetchedFromDb.setStockQuantity(estoqueOriginalNesteTeste);
        productEntityFetchedFromDb.setCategory(existingCategory);

        String updatedName = "Notebook Gamer V2 Atualizado";

        ProductRequestDTO updateRequestDTO = new ProductRequestDTO(
                updatedName,
                "Nova descrição V2, mesma categoria",
                new BigDecimal("780.00"),
                10,
                existingCategoryId // Mesmo ID de categoria
        );

        ProductResponseDTO expectedResponseDTO = new ProductResponseDTO(
                productIdToUpdate,
                updatedName,
                updateRequestDTO.getDescription(),
                updateRequestDTO.getPrice(),
                updateRequestDTO.getStockQuantity(),
                new CategoryResponseDTO(existingCategory.getId(), existingCategory.getName())
        );

        when(productRepository.findById(productIdToUpdate)).thenReturn(Optional.of(productEntityFetchedFromDb));

        doAnswer(invocation -> {
            ProductRequestDTO dtoArg = invocation.getArgument(0);
            Product productToUpdateArg = invocation.getArgument(1); // Este é o productEntityFetchedFromDb
            productToUpdateArg.setName(dtoArg.getName());
            productToUpdateArg.setDescription(dtoArg.getDescription());
            productToUpdateArg.setPrice(dtoArg.getPrice());
            productToUpdateArg.setStockQuantity(dtoArg.getStockQuantity());
            return null;
        }).when(productConverter).updateEntityFromRequestDTO(
                eq(updateRequestDTO),
                eq(productEntityFetchedFromDb),
                eq(existingCategory)
        );

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productConverter.toResponseDTO(eq(productEntityFetchedFromDb))).thenReturn(expectedResponseDTO);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        ProductResponseDTO actualResponseDTO = productServiceImp.updateProduct(productIdToUpdate, updateRequestDTO);

        assertNotNull(actualResponseDTO);
        assertEquals(updatedName, actualResponseDTO.getName());

        verify(productRepository).findById(productIdToUpdate);
        verify(categoryRepository, never()).findById(anyLong());
        verify(productConverter).updateEntityFromRequestDTO(eq(updateRequestDTO), eq(productEntityFetchedFromDb), eq(existingCategory));
        verify(productRepository).save(productCaptor.capture());
        verify(productConverter).toResponseDTO(eq(productEntityFetchedFromDb));

        Product capturedProduct = productCaptor.getValue();
        assertEquals(updatedName, capturedProduct.getName());
        assertEquals(existingCategoryId, capturedProduct.getCategory().getId());
        assertNotNull(capturedProduct.getCategory());

    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar produto com ID inexistente")
    void updateProduct_quandoIdDoProdutoInexistente_deveLancarResourceNotFoundException(){
        when(productRepository.findById(nonExistentProducttId)).thenReturn(Optional.empty());

        ResourceNotFoundException thrownException  = assertThrows(ResourceNotFoundException.class,
                () -> {productServiceImp.updateProduct(nonExistentProducttId, productRequestDTO);
        });

        assertEquals("Produto não encontrado por id: " + nonExistentProducttId, thrownException.getMessage());

        verify(productRepository,times(1)).findById(nonExistentProducttId);

        verify(categoryRepository, never()).findById(anyLong());
        verify(productConverter, never()).updateEntityFromRequestDTO(any(),any(),any());
        verify(productRepository,never()).save(any(Product.class));
        verify(productConverter,never()).toResponseDTO(any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException se a nova categoria (para atualização) não existir")
    void updateProduct_lançaResourceNotFoundException_seNovaCategoriaParaAtualizarNaoExistir(){
        Long productIdToUpdate = existingProductId;
        Long idDaNovaCategoriaInexistente = 999L;

        ProductRequestDTO updateRequestWithNonExistentCategoryDTO = new ProductRequestDTO(
                "Produto com Categoria Nova Inexistente",
                "Tentando atualizar para categoria que não existe",
                new BigDecimal("123.45"),
                5,
                idDaNovaCategoriaInexistente
        );
        Product productFetchedFromDb = new Product();
        productFetchedFromDb.setId(productIdToUpdate);
        productFetchedFromDb.setName("Produto Existente XYZ");
        productFetchedFromDb.setDescription("Descrição do produto existente XYZ");
        productFetchedFromDb.setCategory(existingCategory);

        when(productRepository.findById(productIdToUpdate)).thenReturn(Optional.of(productFetchedFromDb));
        when(categoryRepository.findById(idDaNovaCategoriaInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException thrownException = assertThrows(ResourceNotFoundException.class, () -> {
            productServiceImp.updateProduct(productIdToUpdate, updateRequestWithNonExistentCategoryDTO);
        });

        assertEquals("Categoria para atualização não encontrada com o ID: " + idDaNovaCategoriaInexistente, thrownException.getMessage());

        verify(productRepository, times(1)).findById(productIdToUpdate);
        verify(categoryRepository, times(1)).findById(idDaNovaCategoriaInexistente);
        verify(productConverter, never()).updateEntityFromRequestDTO(any(), any(), any());
        verify(productRepository, never()).save(any(Product.class));
        verify(productConverter, never()).toResponseDTO(any(Product.class));
    }

    @Test
    @DisplayName("Deve excluir o produto com sucesso quando o ID existir")
    void deleteProduct_deveDeletarProduto_seIdExistir(){
        when(productRepository.existsById(existingProductId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(existingProductId);

        assertDoesNotThrow(() -> productServiceImp.deleteProduct(existingProductId));

        verify(productRepository,times(1)).existsById(existingProductId);
        verify(productRepository,times(1)).deleteById(existingProductId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar produto com ID inexistente")
    void deleteProduct_deveLancarResourceNotFoundException_seIdDoProdutoNaoExistir(){
        when(productRepository.existsById(nonExistentProducttId)).thenReturn(false);

        ResourceNotFoundException thrownException = assertThrows(ResourceNotFoundException.class, () -> {
            productServiceImp.deleteProduct(nonExistentProducttId);
        });

        assertEquals("Produto não encontrado pelo ID: " + nonExistentProducttId, thrownException.getMessage());
        verify(productRepository, times(1)).existsById(nonExistentProducttId);
        verify(productRepository, never()).deleteById(nonExistentProducttId);
    }
}
