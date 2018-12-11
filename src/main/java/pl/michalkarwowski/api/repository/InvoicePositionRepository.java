package pl.michalkarwowski.api.repository;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.model.InvoicePosition;

public interface InvoicePositionRepository extends CrudRepository<InvoicePosition, Integer> {
    InvoicePosition findAllByInvoice(String invoiceID);
}
