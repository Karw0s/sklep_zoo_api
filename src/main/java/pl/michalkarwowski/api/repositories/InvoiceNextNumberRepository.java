package pl.michalkarwowski.api.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.models.InvoiceNextNumber;

public interface InvoiceNextNumberRepository extends CrudRepository<InvoiceNextNumber, Long> {
}
