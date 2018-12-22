package pl.michalkarwowski.api.services;

import pl.michalkarwowski.api.dto.products.ProductDTO;
import pl.michalkarwowski.api.models.Product;

import java.util.List;

public interface ProductService {
    List<Product> getUserProducts();
    Product getProduct(Integer id);
    Product createProduct(Product product);
    List<Product> addProductList(List<Product> productList);
    Product updateProduct(Integer id,  ProductDTO product);
    boolean deleteProduct(Integer id);
}
