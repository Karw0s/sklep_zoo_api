package pl.michalkarwowski.api.repository;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.model.Towar;

import java.util.List;

public interface TowarRepository extends CrudRepository <Towar, Integer>  {
    List<Towar> findAll();
    Towar findByName(String name);
}
