package pl.michalkarwowski.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.michalkarwowski.api.model.Faktura;
import pl.michalkarwowski.api.model.Towar;
import pl.michalkarwowski.api.service.FakturaService;

@RestController
public class FakturaController {

    private final FakturaService fakturaService;

    @Autowired
    public FakturaController(FakturaService fakturaService) {
        this.fakturaService = fakturaService;
    }

    @PostMapping("/faktura")
    public ResponseEntity<Faktura> addTowar(@RequestBody Towar towar,
                                            @RequestBody String idFaktura,
                                            @RequestBody Integer ilosc) {
        return new ResponseEntity<>(fakturaService.addNewPosition(towar, idFaktura, ilosc), HttpStatus.OK);

    }
}
