package pl.michalkarwowski.api.repository;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.model.Client;

public interface ClientRepository extends CrudRepository<Client, Integer> {
    Client getById(Integer id);
}
