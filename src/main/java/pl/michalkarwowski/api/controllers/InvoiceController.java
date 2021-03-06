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
import pl.michalkarwowski.api.dto.invoice.InvoiceListDTO;
import pl.michalkarwowski.api.dto.invoice.InvoiceCreateResponseDTO;
import pl.michalkarwowski.api.dto.invoice.InvoiceDTO;
import pl.michalkarwowski.api.dto.invoice.InvoiceNextNumberDTO;
import pl.michalkarwowski.api.dto.invoice.InvoicePositionDTO;
import pl.michalkarwowski.api.exceptions.InvoiceExistsException;
import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.models.InvoicePosition;
import pl.michalkarwowski.api.services.InvoiceService;
import pl.michalkarwowski.api.util.GeneratePdfInvoice;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }


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

        ModelMapper modelMapper2 = new ModelMapper();
        TypeMap<Invoice, InvoiceCreateResponseDTO> typeMap = modelMapper2.createTypeMap(Invoice.class, InvoiceCreateResponseDTO.class);
        typeMap.addMappings(new PropertyMap<Invoice, InvoiceCreateResponseDTO>() {
            @Override
            protected void configure() {
                skip(destination.getPositions());
            }
        });
        InvoiceCreateResponseDTO invoiceCreateResponseDTO = modelMapper2.map(newInvoice, InvoiceCreateResponseDTO.class);
        invoiceCreateResponseDTO.setPositions(new ArrayList<>());
        for (InvoicePosition position : newInvoice.getPositions()) {
            invoiceCreateResponseDTO.getPositions().add(modelMapper2.map(position, InvoicePositionDTO.class));
        }

        return new ResponseEntity<>(invoiceCreateResponseDTO, HttpStatus.CREATED);
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
        for (InvoicePosition position : invoice.getPositions()) {
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
        ModelMapper modelMapper2 = new ModelMapper();
        TypeMap<Invoice, InvoiceDTO> typeMap = modelMapper2.createTypeMap(Invoice.class, InvoiceDTO.class);
        typeMap.addMappings(new PropertyMap<Invoice, InvoiceDTO>() {
            @Override
            protected void configure() {
                skip(destination.getPositions());
            }
        });
        InvoiceDTO updatedInvoiceDTO = modelMapper2.map(updatedInvoice, InvoiceDTO.class);
        updatedInvoiceDTO.setPositions(new ArrayList<>());
        for (InvoicePosition position : updatedInvoice.getPositions()) {
            updatedInvoiceDTO.getPositions().add(modelMapper2.map(position, InvoicePositionDTO.class));
        }
        return new ResponseEntity<>(updatedInvoiceDTO, HttpStatus.OK);
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
    public ResponseEntity<InputStreamResource> invoicePdf(@PathVariable Long id,
                                                          @RequestParam boolean originalPlusCopy) {
        Invoice invoice = invoiceService.getInvoice(id);
        if (invoice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ByteArrayInputStream bis = GeneratePdfInvoice.pdfInvoice(invoice, originalPlusCopy);
        String filename = String.format("faktura_nr_%s.pdf", invoice.getNumber().replace("/", "-"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + filename);
        headers.add("Content-Filename", filename);
        headers.add("Access-Control-Expose-Headers", "Content-Length, Content-Disposition, Content-Filename");
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorMessage.builder().errorField("RequestParam").message("Canot Parse RequestParam").build());
        }
        return new ResponseEntity<>(InvoiceNextNumberDTO.builder().number(invoiceService.nextInvoiceNumber(issueDateInvoice)).build(), HttpStatus.OK);
    }
}
