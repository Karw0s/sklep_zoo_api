package pl.michalkarwowski.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.model.Invoice;
import pl.michalkarwowski.api.model.Product;
import pl.michalkarwowski.api.service.InvoiceService;

@RestController("/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
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

    @PostMapping("/create")
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable String id) {
        Invoice invoice = invoiceService.getInvoice(id);
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }
}
