package br.com.josewolf.catalogoprodutoapi.business;

import br.com.josewolf.catalogoprodutoapi.business.dto.request.ProductRequestDTO;
import br.com.josewolf.catalogoprodutoapi.business.dto.response.ProductResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    List<ProductResponseDTO> getAllProducts();
    Optional<ProductResponseDTO> getProductById(Long id);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);
    void deleteProduct(Long id);

}
