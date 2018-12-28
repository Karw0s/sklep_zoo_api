package pl.michalkarwowski.api.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.michalkarwowski.api.models.InvoicePosition;

import java.util.List;

public interface InvoicePositionRepository extends CrudRepository<InvoicePosition, Long> {
    List<InvoicePosition> findAllByProductId(Long id);
}
