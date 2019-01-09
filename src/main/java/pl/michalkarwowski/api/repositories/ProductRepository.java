package pl.michalkarwowski.api.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.models.Product;

import java.util.List;

public interface ProductRepository extends CrudRepository <Product, Integer>  {
    List<Product> findAll();
    Product getById(Integer id);
    void deleteById(Integer id);
}
