package pl.michalkarwowski.api.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.models.InvoicePosition;

public interface InvoicePositionRepository extends CrudRepository<InvoicePosition, Integer> {
    InvoicePosition findAllByInvoice(String invoiceID);
}
