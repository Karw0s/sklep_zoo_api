package pl.michalkarwowski.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.dto.InvoiceListDTO;
import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.models.Product;
import pl.michalkarwowski.api.services.InvoiceService;

import java.util.List;

@RestController
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<Invoice>> getUserInvoices() {
        List<Invoice> invoiceList = invoiceService.getUserInvoices();
        return new ResponseEntity<>(invoiceList, HttpStatus.OK);
    }

    @PostMapping("/invoices")
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        Invoice newInvoice = invoiceService.createInvoice(invoice);
        return new ResponseEntity<>(newInvoice, HttpStatus.OK);
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoice(id);
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }

    @PutMapping("/invoices/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id,
                                                 @RequestBody Invoice invoice) {
        Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
        if (updatedInvoice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return new ResponseEntity<>(updatedInvoice, HttpStatus.OK);
    }

    @DeleteMapping("/invoices/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        if (invoiceService.deleteInvoice(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/invoices/list")
    public ResponseEntity<List<InvoiceListDTO>> getInvoicesList() {
        List<InvoiceListDTO> invoiceListDTO = invoiceService.getInvoiceList();
        return new ResponseEntity<>(invoiceListDTO, HttpStatus.OK);
    }

    @PostMapping("/invoices/addProduct")
    public ResponseEntity<Invoice> addInvoicePosition(@RequestBody String invoiceID,
                                                      @RequestBody Product product,
                                                      @RequestBody Integer amount) {
        return new ResponseEntity<>(invoiceService.addNewPosition(product, invoiceID, amount), HttpStatus.OK);
    }

    @GetMapping("/invoices/getNextInvoiceId")
    public ResponseEntity<String> getNextInvoiceId() {
        return null;
    }
}
