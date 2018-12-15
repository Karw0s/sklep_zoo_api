package pl.michalkarwowski.api.service;

import pl.michalkarwowski.api.model.Product;

import java.util.List;

public interface ProductService {
    Product addNewProduct(Product product, String username);
    List<Product> addProductList(List<Product> productList);
    List<Product> getUserProducts();
    Product getProduct(String name);
    Product updateProduct(Product product);
    Product getProduct(Integer id);
    boolean deleteProduct(Integer id);
}
