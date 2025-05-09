package br.com.josewolf.catalogoprodutoapi.business;

import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product createProduct(Product product);
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    Product updateProduct(Long id, Product productDetails);
    void deleteProduct(Long id);

}
