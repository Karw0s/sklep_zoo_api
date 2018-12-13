package pl.michalkarwowski.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.michalkarwowski.api.model.ApplicationUser;
import pl.michalkarwowski.api.model.Product;

import java.util.List;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findByUsername(String username);
}
