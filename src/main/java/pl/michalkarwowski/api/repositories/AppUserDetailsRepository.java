package pl.michalkarwowski.api.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.models.AppUserDetails;

public interface AppUserDetailsRepository extends CrudRepository<AppUserDetails, Integer> {
}
