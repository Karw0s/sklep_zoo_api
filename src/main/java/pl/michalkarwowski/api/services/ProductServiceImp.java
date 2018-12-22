package pl.michalkarwowski.api.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.dto.products.ProductDTO;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.models.Product;
import pl.michalkarwowski.api.repositories.ProductRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;
    private final ApplicationUserService applicationUserService;
    private ModelMapper modelMapper;

    @Autowired
    public ProductServiceImp(ProductRepository productRepository,
                             ApplicationUserService applicationUserService,
                             ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.applicationUserService = applicationUserService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Product> getUserProducts() {
        List<Product> products = applicationUserService.getCurrentUser().getProducts();
        products.sort(Comparator.comparing(Product::getName));
        return products;
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
    public Product createProduct(Product product) {
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
    public Product updateProduct(Integer id, ProductDTO productDTO) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        Product newProduct = modelMapper.map(productDTO, Product.class);
        newProduct.setId(id);
        int productIndex = applicationUser.getProducts().indexOf(productRepository.getById(id));
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
