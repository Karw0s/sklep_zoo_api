package pl.michalkarwowski.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.model.Faktura;
import pl.michalkarwowski.api.model.Product;
import pl.michalkarwowski.api.service.FakturaService;

@RestController("/faktura")
public class FakturaController {

    private final FakturaService fakturaService;

    @Autowired
    public FakturaController(FakturaService fakturaService) {
        this.fakturaService = fakturaService;
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Faktura> addPozycjaToFaktura(@RequestBody String idFaktura,
                                                       @RequestBody Product product,
                                                       @RequestBody Integer ilosc) {
        return new ResponseEntity<>(fakturaService.addNewPosition(product, idFaktura, ilosc), HttpStatus.OK);
    }

    @GetMapping("/getNextFakturaId")
    public ResponseEntity<String> getNextFakturaId() {
        return null;
    }

    @PostMapping("/create")
    public ResponseEntity<Faktura> createFaktura(@RequestBody Faktura faktura) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faktura> getFaktura(@PathVariable String id) {
        Faktura faktura = fakturaService.getFaktura(id);
        return new ResponseEntity<>(faktura, HttpStatus.OK);
    }
}
