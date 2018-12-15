package pl.michalkarwowski.api.service;

import pl.michalkarwowski.api.model.Invoice;
import pl.michalkarwowski.api.model.Product;

import java.util.List;

public interface InvoiceService {
    Invoice addNewPosition(Product product, String invoiceID, Integer amount);
    Invoice getInvoice(String invoiceNumber);
    Invoice createInvoice(Invoice invoice, String username);
    Invoice updateInvoice(Invoice invoice, String username);
    List<Invoice> getUserInvoices(String username);
}
