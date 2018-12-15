package pl.michalkarwowski.api.services;

import pl.michalkarwowski.api.dto.InvoiceListDTO;
import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.models.Product;

import java.util.List;

public interface InvoiceService {
    Invoice createInvoice(Invoice invoice);
    Invoice getInvoice(Long invoiceNumber);
    List<Invoice> getUserInvoices();
    Invoice updateInvoice(Invoice invoice);
    boolean deleteInvoice(Long id);
    Invoice addNewPosition(Product product, String invoiceID, Integer amount);
    List<InvoiceListDTO> getInvoiceList();
}
