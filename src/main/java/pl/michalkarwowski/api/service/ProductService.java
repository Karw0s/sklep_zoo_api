package pl.michalkarwowski.api.service;

import pl.michalkarwowski.api.model.Product;

import java.util.List;

public interface ProductService {
    Product addNewProduct(Product product, String username);
    List<Product> addProductList(List<Product> productList, String username);
    List<Product> getUserProducts(String username);
    Product getProduct(String name);
    Product updateProduct(Product product, String username);
    Product getProduct(Integer id, String username);
    boolean deleteProduct(Integer id, String username);
}
