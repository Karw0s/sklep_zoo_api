package pl.michalkarwowski.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.model.Product;
import pl.michalkarwowski.api.repository.ProductRepository;

import java.util.List;

@Service
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImp(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public Product addNewProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProduct(String name) {
        return productRepository.findByName(name);
    }
}
