package pl.michalkarwowski.api.repository;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.model.Address;

public interface AddressRepository extends CrudRepository<Address, Integer> {
}
