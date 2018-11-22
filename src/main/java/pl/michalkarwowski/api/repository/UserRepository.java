package pl.michalkarwowski.api.repository;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByNameAndEmail(String name, String email);
}
