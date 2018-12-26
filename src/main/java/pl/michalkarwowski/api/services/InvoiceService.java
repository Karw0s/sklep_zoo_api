package pl.michalkarwowski.api.services;

import pl.michalkarwowski.api.dto.InvoiceListDTO;
import pl.michalkarwowski.api.dto.invoice.InvoiceDTO;
import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.models.Product;

import java.util.List;

public interface InvoiceService {
    Invoice createInvoice(InvoiceDTO invoiceDTO);
    Invoice getInvoice(Long invoiceNumber);
    Invoice updateInvoice(Long id, InvoiceDTO invoice);
    boolean deleteInvoice(Long id);
    Invoice addNewPosition(Product product, String invoiceID, Integer amount);
    List<InvoiceListDTO> getInvoiceList();
    String nextInvoiceNumber();
}
