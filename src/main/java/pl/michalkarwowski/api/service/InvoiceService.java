package pl.michalkarwowski.api.service;

import pl.michalkarwowski.api.model.Invoice;
import pl.michalkarwowski.api.model.Product;

import java.util.List;

public interface InvoiceService {
    Invoice createInvoice(Invoice invoice);
    Invoice getInvoice(Long invoiceNumber);
    List<Invoice> getUserInvoices();
    Invoice updateInvoice(Invoice invoice);
    boolean deleteInvoice(Long id);
    Invoice addNewPosition(Product product, String invoiceID, Integer amount);
}
