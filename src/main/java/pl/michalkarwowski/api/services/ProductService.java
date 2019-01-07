package pl.michalkarwowski.api.services;

import org.springframework.web.multipart.MultipartFile;
import pl.michalkarwowski.api.dto.products.ProductDTO;
import pl.michalkarwowski.api.exceptions.InvalidCSVException;
import pl.michalkarwowski.api.models.Product;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<Product> getUserProducts();
    Product getProduct(Integer id);
    Product createProduct(Product product);
    List<Product> addProductListFromCSV(MultipartFile file) throws IOException, InvalidCSVException;
    Product updateProduct(Integer id,  ProductDTO product);
    boolean deleteProduct(Integer id);
}
