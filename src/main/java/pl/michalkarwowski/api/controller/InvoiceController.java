package pl.michalkarwowski.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.model.Invoice;
import pl.michalkarwowski.api.model.Product;
import pl.michalkarwowski.api.service.InvoiceService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController("/invoices")
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

    @PostMapping()
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        Invoice newInvoice = invoiceService.createInvoice(invoice, username);
        return new ResponseEntity<>(newInvoice, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable String id) {
        Invoice invoice = invoiceService.getInvoice(id);
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable String id,
                                                 @RequestBody Invoice invoice,
                                                 HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        Invoice updatedInvoice = invoiceService.updateInvoice(invoice, username);
        return new ResponseEntity<>(updatedInvoice, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getUserInvoices(HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        List<Invoice> invoiceList = invoiceService.getUserInvoices(username);
        return new ResponseEntity<>(invoiceList, HttpStatus.OK);
    }
}
