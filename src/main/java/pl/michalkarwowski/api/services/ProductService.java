package pl.michalkarwowski.api.services;

import pl.michalkarwowski.api.models.Product;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product, String username);
    List<Product> addProductList(List<Product> productList);
    List<Product> getUserProducts();
    Product getProduct(String name);
    Product updateProduct(Product product);
    Product getProduct(Integer id);
    boolean deleteProduct(Integer id);
}
