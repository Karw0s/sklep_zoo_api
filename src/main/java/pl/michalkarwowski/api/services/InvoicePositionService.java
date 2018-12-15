package pl.michalkarwowski.api.services;

import pl.michalkarwowski.api.models.InvoicePosition;

import java.util.List;

public interface InvoicePositionService {
    List<InvoicePosition> createInvoicePos(List<InvoicePosition> invoicePositions);
}
