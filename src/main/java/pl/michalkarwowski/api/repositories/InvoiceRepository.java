package pl.michalkarwowski.api.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.models.Invoice;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    Invoice getById(Long id);
    Invoice getByNumber (String number);

}
