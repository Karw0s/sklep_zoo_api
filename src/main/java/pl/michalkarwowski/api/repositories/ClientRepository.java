package pl.michalkarwowski.api.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.models.Client;

public interface ClientRepository extends CrudRepository<Client, Integer> {
}
