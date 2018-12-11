package pl.michalkarwowski.api.service;

import pl.michalkarwowski.api.model.Invoice;
import pl.michalkarwowski.api.model.Product;

public interface InvoiceService {
    Invoice addNewPosition(Product product, String invoiceID, Integer amount);
    Invoice getInvoice(String invoiceNumber);
}
