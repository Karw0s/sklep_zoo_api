package pl.michalkarwowski.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.model.ApplicationUser;
import pl.michalkarwowski.api.model.Product;
import pl.michalkarwowski.api.repository.ProductRepository;

import java.util.List;

@Service
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public ProductServiceImp(ProductRepository productRepository,
                             ApplicationUserService applicationUserService) {
        this.productRepository = productRepository;
        this.applicationUserService = applicationUserService;
    }

    @Override
    public Product addNewProduct(Product product, String username) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        applicationUser.getProducts().add(product);
        productRepository.save(product);
        applicationUserService.saveAppUser(applicationUser);
        return product;
    }

    @Override
    public List<Product> addProductList(List<Product> productList) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        applicationUser.getProducts().addAll(productList);
        List<Product> products = (List<Product>) productRepository.saveAll(productList);
        applicationUserService.saveAppUser(applicationUser);
        return products;
    }

    @Override
    public List<Product> getUserProducts() {
        return applicationUserService.getCurrentUser().getProducts();
    }

    @Override
    public Product getProduct(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public Product updateProduct(Product newProduct) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        int productIndex = applicationUser.getProducts().indexOf(productRepository.getById(newProduct.getId()));
        Product product = null;
        if (productIndex != -1)
            if (!applicationUser.getProducts().get(productIndex).equals(newProduct)) {
                applicationUser.getProducts().remove(productIndex);

                product = productRepository.save(newProduct);
                applicationUser.getProducts().add(product);
                applicationUserService.saveAppUser(applicationUser);
            }

        return product;
    }

    @Override
    public Product getProduct(Integer id) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        Product product = productRepository.getById(id);
        if (applicationUser.getProducts().contains(product)) {
            return product;
        } else {
            return null;
        }
//        return productRepository.getById(id);
    }

    @Override
    public boolean deleteProduct(Integer id) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        Product product = productRepository.getById(id);
        if (applicationUser.getProducts().contains(product)) {
            if (applicationUser.getProducts().remove(productRepository.getById(id))) {
                applicationUserService.saveAppUser(applicationUser);
                productRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }
}
