package pl.michalkarwowski.api.repository;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.model.Product;

import java.util.List;

public interface ProductRepository extends CrudRepository <Product, Integer>  {
    List<Product> findAll();
    Product findByName(String name);
}
