package pl.michalkarwowski.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.model.Invoice;
import pl.michalkarwowski.api.model.InvoicePosition;
import pl.michalkarwowski.api.model.Product;
import pl.michalkarwowski.api.repository.InvoiceRepository;
import pl.michalkarwowski.api.repository.InvoicePositionRepository;

@Service
public class InvoiceServiceImp implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoicePositionRepository invoicePositionRepository;

    @Autowired
    public InvoiceServiceImp(InvoiceRepository invoiceRepository, InvoicePositionRepository invoicePositionRepository) {
        this.invoiceRepository = invoiceRepository;
        this.invoicePositionRepository = invoicePositionRepository;
    }

    @Override
    public Invoice addNewPosition(Product product, String invoiceID, Integer amount) {
//        Invoice invoice = invoiceRepository.findByNr(invoiceID);
//        InvoicePosition invoicePosition = new InvoicePosition();
//        invoicePosition.setInvoice(invoice);
//        invoicePosition.setProduct(product);
//        invoicePosition.setAmount(amount);
//        invoicePosition = invoicePositionRepository.save(invoicePosition);
//        invoice.getInvoicePosition().add(invoicePosition);
        return null;
    }

    @Override
    public Invoice getInvoice(String invoiceNumber) {
        return invoiceRepository.getById(invoiceNumber);
    }
}
