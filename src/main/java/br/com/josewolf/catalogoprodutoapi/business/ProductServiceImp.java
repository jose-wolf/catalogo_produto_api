package br.com.josewolf.catalogoprodutoapi.business;

import br.com.josewolf.catalogoprodutoapi.business.converter.ProductConverter;
import br.com.josewolf.catalogoprodutoapi.business.dto.request.ProductRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.ProductResponseDTO;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Product;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.repository.CategoryRepository;
import br.com.josewolf.catalogoprodutoapi.infraestrutura.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductConverter productConverter;

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com o ID: " + productRequestDTO.getCategoryId()));

        Product productEntity = productConverter.toEntity(productRequestDTO, category);
        Product savedProduct = productRepository.save(productEntity);
        return productConverter.toResponseDTO(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productConverter.toResponseDTOList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponseDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .map(productConverter::toResponseDTO);
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Produto não encontrado por id: " + id));

        Category categoryToAssociate = product.getCategory();

        if(productRequestDTO.getCategoryId() != null){
            if(product.getCategory() == null || !product.getCategory().getId().equals(productRequestDTO.getCategoryId())){
                Category managedCategory = categoryRepository.findById(productRequestDTO.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Categoria para atualização não encontrada com o ID: " + productRequestDTO.getCategoryId()));
                product.setCategory(managedCategory);
            }
        }

        productConverter.updateEntityFromRequestDTO(productRequestDTO,product, categoryToAssociate);
        Product updateProduct = productRepository.save(product);

        return productConverter.toResponseDTO(updateProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if(!productRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado pelo ID: " + id);
        }
        productRepository.deleteById(id);
    }
}
