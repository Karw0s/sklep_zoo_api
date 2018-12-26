package pl.michalkarwowski.api.controllers;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.dto.InvoiceListDTO;
import pl.michalkarwowski.api.dto.invoice.InvoiceDTO;
import pl.michalkarwowski.api.dto.invoice.InvoiceNextNumberDTO;
import pl.michalkarwowski.api.dto.invoice.InvoicePositionDTO;
import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.models.InvoicePosition;
import pl.michalkarwowski.api.models.Product;
import pl.michalkarwowski.api.services.InvoiceService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class InvoiceController {

    private final InvoiceService invoiceService;
    private ModelMapper modelMapper;

    @Autowired
    public InvoiceController(InvoiceService invoiceService,
                             ModelMapper modelMapper) {
        this.invoiceService = invoiceService;
        this.modelMapper = modelMapper;
    }

//    @GetMapping("/invoices")
//    public ResponseEntity<List<Invoice>> getUserInvoices() {
//        List<Invoice> invoiceList = invoiceService.getUserInvoices();
//        return new ResponseEntity<>(invoiceList, HttpStatus.OK);
//    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceListDTO>> getInvoicesList() {
        List<InvoiceListDTO> invoiceListDTO = invoiceService.getInvoiceList();
        return new ResponseEntity<>(invoiceListDTO, HttpStatus.OK);
    }

    @PostMapping("/invoices")
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO) {
        Invoice newInvoice = invoiceService.createInvoice(invoiceDTO);
        return new ResponseEntity<>(newInvoice, HttpStatus.CREATED);
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<InvoiceDTO> getInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoice(id);
        ModelMapper modelMapper2 = new ModelMapper();
        TypeMap<Invoice, InvoiceDTO> typeMap = modelMapper2.createTypeMap(Invoice.class, InvoiceDTO.class);
        typeMap.addMappings(new PropertyMap<Invoice, InvoiceDTO>() {
            @Override
            protected void configure() {
                skip(destination.getPositions());
            }
        });
        InvoiceDTO invoiceDTO = modelMapper2.map(invoice, InvoiceDTO.class);
        invoiceDTO.setPositions(new ArrayList<>());
        for (InvoicePosition position :
                invoice.getPositions()) {
            invoiceDTO.getPositions().add(modelMapper2.map(position, InvoicePositionDTO.class));
        }
        return new ResponseEntity<>(invoiceDTO, HttpStatus.OK);
    }

    @PutMapping("/invoices/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id,
                                                 @Valid @RequestBody Invoice invoice) {
//        Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
//        if (updatedInvoice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//        return new ResponseEntity<>(updatedInvoice, HttpStatus.OK);
    }

    @DeleteMapping("/invoices/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        if (invoiceService.deleteInvoice(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }



    @PostMapping("/invoices/addProduct")
    public ResponseEntity<Invoice> addInvoicePosition(@RequestBody String invoiceID,
                                                      @RequestBody Product product,
                                                      @RequestBody Integer amount) {
        return new ResponseEntity<>(invoiceService.addNewPosition(product, invoiceID, amount), HttpStatus.OK);
    }

    @GetMapping("/invoices/next-number")
    public ResponseEntity<InvoiceNextNumberDTO> getNextInvoiceNumber() {
        return null;
    }
}
