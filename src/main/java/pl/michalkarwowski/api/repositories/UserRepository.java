package pl.michalkarwowski.api.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.models.ApplicationUser;


public interface UserRepository extends CrudRepository<ApplicationUser, Long>{

    ApplicationUser findByUsername(String username);

}
