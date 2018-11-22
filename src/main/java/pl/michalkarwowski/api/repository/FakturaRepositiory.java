package pl.michalkarwowski.api.repository;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.model.Faktura;

public interface FakturaRepositiory extends CrudRepository<Faktura, String> {
    Faktura findByNr(String nr);
}
