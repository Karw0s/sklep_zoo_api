package pl.michalkarwowski.api.repository;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.model.ApplicationUser;
import pl.michalkarwowski.api.model.Product;


public interface UserRepository extends CrudRepository<ApplicationUser, Long>{

    ApplicationUser findByUsername(String username);

}
