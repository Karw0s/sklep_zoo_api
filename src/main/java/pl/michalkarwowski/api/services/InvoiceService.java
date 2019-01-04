package pl.michalkarwowski.api.services;

import pl.michalkarwowski.api.dto.invoice.InvoiceListDTO;
import pl.michalkarwowski.api.dto.invoice.InvoiceDTO;
import pl.michalkarwowski.api.exceptions.InvoiceExistsException;
import pl.michalkarwowski.api.models.Invoice;

import java.util.Date;
import java.util.List;

public interface InvoiceService {
    Invoice createInvoice(InvoiceDTO invoiceDTO) throws InvoiceExistsException;
    Invoice getInvoice(Long invoiceNumber);
    Invoice updateInvoice(Long id, InvoiceDTO invoice) throws InvoiceExistsException;
    boolean deleteInvoice(Long id);
    List<InvoiceListDTO> getInvoiceList();
    String nextInvoiceNumber(Date issueDate);
}
