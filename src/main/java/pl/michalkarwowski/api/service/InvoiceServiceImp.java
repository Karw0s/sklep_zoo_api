package pl.michalkarwowski.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.model.ApplicationUser;
import pl.michalkarwowski.api.model.Invoice;
import pl.michalkarwowski.api.model.InvoicePosition;
import pl.michalkarwowski.api.model.Product;
import pl.michalkarwowski.api.repository.ApplicationUserRepository;
import pl.michalkarwowski.api.repository.InvoiceRepository;
import pl.michalkarwowski.api.repository.InvoicePositionRepository;
import pl.michalkarwowski.api.repository.UserRepository;

import java.util.List;

@Service
public class InvoiceServiceImp implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public InvoiceServiceImp(InvoiceRepository invoiceRepository,
                             ApplicationUserService applicationUserService) {
        this.invoiceRepository = invoiceRepository;
        this.applicationUserService = applicationUserService;
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

    @Override
    public Invoice createInvoice(Invoice invoice, String username) {
        ApplicationUser user = applicationUserService.getCurrentUser();
        user.getInvoices().add(invoice);
        Invoice invoice1 = invoiceRepository.save(invoice);
        applicationUserService.saveAppUser(user);
        return invoice1;
    }

    @Override
    public Invoice updateInvoice(Invoice invoice, String username) {
        return null;
    }

    @Override
    public List<Invoice> getUserInvoices(String username) {
        return applicationUserService.getCurrentUser().getInvoices();
    }
}
