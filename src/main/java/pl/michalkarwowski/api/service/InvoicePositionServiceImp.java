package pl.michalkarwowski.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.model.InvoicePosition;
import pl.michalkarwowski.api.repository.InvoicePositionRepository;

import java.util.List;

@Service
public class InvoicePositionServiceImp implements InvoicePositionService {
    private final InvoicePositionRepository invoicePosRepository;

    @Autowired
    public InvoicePositionServiceImp(InvoicePositionRepository invoicePosRepository) {
        this.invoicePosRepository = invoicePosRepository;
    }

    @Override
    public List<InvoicePosition> createInvoicePos(List<InvoicePosition> invoicePositions) {
        return (List<InvoicePosition>) invoicePosRepository.saveAll(invoicePositions);
    }
}
