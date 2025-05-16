package br.com.josewolf.catalogoprodutoapi.business;

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

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if(product.getCategory() == null || product.getCategory().getId() == null){
            throw new IllegalArgumentException("A categoria do produto não pode ser nula e deve ter um ID.");
        }
        Category managedCategory = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com o ID: " + product.getCategory().getId()));

        product.setCategory(managedCategory);
        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Produto não encontrado por id: " + id));
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());
        product.setStockQuantity(productDetails.getStockQuantity());

        if(productDetails.getCategory() != null && productDetails.getCategory().getId() != null){
            Long newCategoryId = productDetails.getCategory().getId();

            if(product.getCategory() == null || !product.getCategory().getId().equals(newCategoryId)){
                Category managedCategory = categoryRepository.findById(newCategoryId)
                        .orElseThrow(() -> new RuntimeException("Categoria para atualização não encontrada com o ID: " + newCategoryId));
                product.setCategory(managedCategory);
            }
        }
        return productRepository.save(product);
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
