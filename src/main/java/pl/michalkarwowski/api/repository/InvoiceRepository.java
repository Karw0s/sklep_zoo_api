package pl.michalkarwowski.api.repository;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.model.Invoice;

public interface InvoiceRepository extends CrudRepository<Invoice, String> {
    Invoice getById(String nr);
    Invoice getByNumber (String number);

}
