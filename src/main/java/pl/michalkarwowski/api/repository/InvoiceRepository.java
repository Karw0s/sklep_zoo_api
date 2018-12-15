package pl.michalkarwowski.api.repository;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.model.Invoice;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    Invoice getById(Long id);
    Invoice getByNumber (String number);

}
