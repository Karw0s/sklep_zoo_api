package pl.michalkarwowski.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.model.ApplicationUser;
import pl.michalkarwowski.api.model.Product;
import pl.michalkarwowski.api.repository.ApplicationUserRepository;
import pl.michalkarwowski.api.repository.ProductRepository;

import java.util.List;

@Service
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;
    private final ApplicationUserRepository applicationUserRepository;

    @Autowired
    public ProductServiceImp(ProductRepository productRepository, ApplicationUserRepository applicationUserRepository) {
        this.productRepository = productRepository;
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public Product addNewProduct(Product product, String username) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        applicationUser.getProducts().add(product);
        productRepository.save(product);
        applicationUserRepository.save(applicationUser);
        return product;
    }

    @Override
    public List<Product> addProductList(List<Product> productList, String username) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        applicationUser.getProducts().addAll(productList);
        List<Product> products = (List<Product>) productRepository.saveAll(productList);
        applicationUserRepository.save(applicationUser);
        return products;
    }

    @Override
    public List<Product> getUserProducts(String username) {
        return applicationUserRepository.findByUsername(username).getProducts();
    }

    @Override
    public Product getProduct(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public Product updateProduct(Product newProduct, String username) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        int productIndex = applicationUser.getProducts().indexOf(productRepository.getById(newProduct.getId()));
        Product product = null;
        if (productIndex != -1)
            if (!applicationUser.getProducts().get(productIndex).equals(newProduct)) {
                applicationUser.getProducts().remove(productIndex);

                product = productRepository.save(newProduct);
                applicationUser.getProducts().add(product);
                applicationUserRepository.save(applicationUser);
            }

        return product;
    }

    @Override
    public Product getProduct(Integer id, String username) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        Product product = productRepository.getById(id);
        if (applicationUser.getProducts().contains(product)) {
            return product;
        } else {
            return null;
        }
//        return productRepository.getById(id);
    }

    @Override
    public boolean deleteProduct(Integer id, String username) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        Product product = productRepository.getById(id);
        if (applicationUser.getProducts().contains(product)) {
            if (applicationUser.getProducts().remove(productRepository.getById(id))) {
                applicationUserRepository.save(applicationUser);
                productRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }
}
