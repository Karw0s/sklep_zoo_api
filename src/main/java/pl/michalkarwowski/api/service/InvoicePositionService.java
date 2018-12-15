package pl.michalkarwowski.api.service;

import pl.michalkarwowski.api.model.InvoicePosition;

import java.util.List;

public interface InvoicePositionService {
    List<InvoicePosition> createInvoicePos(List<InvoicePosition> invoicePositions);
}
