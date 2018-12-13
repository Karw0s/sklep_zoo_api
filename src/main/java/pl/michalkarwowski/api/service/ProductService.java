package pl.michalkarwowski.api.service;

import pl.michalkarwowski.api.model.Product;

import java.util.List;

public interface ProductService {
    Product addNewProduct(Product product);
    List<Product> getProducts();
    Product getProduct(String name);
    Product updateProduct(Integer id, Product product);
    Product getProduct(Integer id);
    void deleteProduct(Integer id);
}
