package pl.michalkarwowski.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.model.Invoice;
import pl.michalkarwowski.api.model.Product;
import pl.michalkarwowski.api.service.InvoiceService;

import java.util.List;

@RestController("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getUserInvoices() {
        List<Invoice> invoiceList = invoiceService.getUserInvoices();
        return new ResponseEntity<>(invoiceList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        Invoice newInvoice = invoiceService.createInvoice(invoice);
        return new ResponseEntity<>(newInvoice, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable String id) {
        Invoice invoice = invoiceService.getInvoice(id);
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable String id,
                                                 @RequestBody Invoice invoice) {
        Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
        if (updatedInvoice == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return new ResponseEntity<>(updatedInvoice, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable String id) {
        if (invoiceService.deleteInvoice(id)){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Invoice> addInvoicePosition(@RequestBody String invoiceID,
                                                      @RequestBody Product product,
                                                      @RequestBody Integer amount) {
        return new ResponseEntity<>(invoiceService.addNewPosition(product, invoiceID, amount), HttpStatus.OK);
    }

    @GetMapping("/getNextInvoiceId")
    public ResponseEntity<String> getNextInvoiceId() {
        return null;
    }
}
