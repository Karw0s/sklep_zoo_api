package pl.michalkarwowski.api.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.models.Invoice;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    List<Invoice> findAllByBuyerId(Integer id);
}
