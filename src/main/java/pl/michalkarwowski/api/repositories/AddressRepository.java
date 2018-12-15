package pl.michalkarwowski.api.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.models.Address;

public interface AddressRepository extends CrudRepository<Address, Integer> {
}
