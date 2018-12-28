package pl.michalkarwowski.api.services;

import com.itextpdf.text.Document;
import pl.michalkarwowski.api.dto.InvoiceListDTO;
import pl.michalkarwowski.api.dto.invoice.InvoiceDTO;
import pl.michalkarwowski.api.exceptions.InvoiceExistsException;
import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.models.Product;

import java.util.Date;
import java.util.List;

public interface InvoiceService {
    Invoice createInvoice(InvoiceDTO invoiceDTO) throws InvoiceExistsException;
    Invoice getInvoice(Long invoiceNumber);
    Invoice updateInvoice(Long id, InvoiceDTO invoice);
    boolean deleteInvoice(Long id);
    Invoice addNewPosition(Product product, String invoiceID, Integer amount);
    List<InvoiceListDTO> getInvoiceList();
    String nextInvoiceNumber(Date issueDate);
    Document generateInvoicePDF(Long id);
}
