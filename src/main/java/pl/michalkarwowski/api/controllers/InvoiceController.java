package pl.michalkarwowski.api.controllers;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.dto.ErrorMessage;
import pl.michalkarwowski.api.dto.InvoiceListDTO;
import pl.michalkarwowski.api.dto.invoice.InvoiceDTO;
import pl.michalkarwowski.api.dto.invoice.InvoiceNextNumberDTO;
import pl.michalkarwowski.api.dto.invoice.InvoicePositionDTO;
import pl.michalkarwowski.api.exceptions.InvoiceExistsException;
import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.models.InvoicePosition;
import pl.michalkarwowski.api.models.Product;
import pl.michalkarwowski.api.services.InvoiceService;
import pl.michalkarwowski.api.util.GeneratePdfInvoice;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.websocket.server.PathParam;
import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
    public ResponseEntity<?> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO) {
        Invoice newInvoice = null;
        try {
            newInvoice = invoiceService.createInvoice(invoiceDTO);
        } catch (InvoiceExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ErrorMessage.builder().errorField("number").message(e.getMessage()).build());
        }
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
    public ResponseEntity<?> updateInvoice(@PathVariable Long id,
                                           @Valid @RequestBody InvoiceDTO invoiceDTO) {
        Invoice updatedInvoice = null;
        try {
            updatedInvoice = invoiceService.updateInvoice(id, invoiceDTO);
        } catch (InvoiceExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ErrorMessage.builder().errorField("number").message(e.getMessage()).build());
        }
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

    @GetMapping("/invoices/{id}/pdf")
    public ResponseEntity<InputStreamResource> invoicePdf(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoice(id);
        if (invoice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ByteArrayInputStream bis = GeneratePdfInvoice.pdfInvoice(invoice);

        String filename = "faktura_nr_" + invoice.getNumber().replace("/", "-") + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + filename);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/invoices/next-number")
    public ResponseEntity<?> getNextInvoiceNumber(@RequestParam String issueDate) {
        Date issueDateInvoice = null;
        try {
            issueDateInvoice = DateFormat.getDateInstance().parse(issueDate);
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(ErrorMessage.builder().errorField("RequestParam").message("Canot Parse RequestParam").build());
        }
        return new ResponseEntity<>(InvoiceNextNumberDTO.builder().number(invoiceService.nextInvoiceNumber(issueDateInvoice)).build(), HttpStatus.OK);
    }
}
