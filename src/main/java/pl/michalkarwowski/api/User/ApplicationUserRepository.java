package pl.michalkarwowski.api.User;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.michalkarwowski.api.model.ApplicationUser;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findByUsername(String username);
}
