package pl.michalkarwowski.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.michalkarwowski.api.models.ApplicationUser;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findByUsername(String username);
    ApplicationUser findByEmail(String email);
}
