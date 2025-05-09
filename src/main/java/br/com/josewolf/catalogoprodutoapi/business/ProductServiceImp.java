package br.com.josewolf.catalogoprodutoapi.business;

import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Product;
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


    @Override
    @Transactional
    public Product createProduct(Product product) {
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
    @Transactional(readOnly = true)
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Produto não encontrado por id: " + id));
        product.setName(productDetails.getName());
        product.setCategory(productDetails.getCategory());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());
        product.setStockQuantity(productDetails.getStockQuantity());

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
