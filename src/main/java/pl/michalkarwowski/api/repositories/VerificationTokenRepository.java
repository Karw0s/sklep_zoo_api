package pl.michalkarwowski.api.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.models.VerificationToken;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    VerificationToken findByUser(ApplicationUser user);

}
