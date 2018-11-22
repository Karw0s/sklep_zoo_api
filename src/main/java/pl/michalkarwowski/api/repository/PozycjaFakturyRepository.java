package pl.michalkarwowski.api.repository;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.model.PozycjaFaktury;

public interface PozycjaFakturyRepository extends CrudRepository<PozycjaFaktury, Integer> {
}
