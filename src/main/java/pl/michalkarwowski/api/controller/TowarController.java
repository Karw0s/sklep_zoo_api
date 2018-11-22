package pl.michalkarwowski.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.model.Towar;
import pl.michalkarwowski.api.service.TowarService;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class TowarController {

    private final TowarService towarService;

    @Autowired
    public TowarController(TowarService towarService) {
        this.towarService = towarService;
    }

    @GetMapping("/towar")
    public ResponseEntity<List<Towar>> getTowar() {
        return new ResponseEntity<>(towarService.getTowary(), HttpStatus.OK);
    }

    @PostMapping("/towar/add")
    public ResponseEntity<Towar> addTowar(@RequestBody Towar towar) {
        towar = towarService.addNewTowar(towar);

        return new ResponseEntity<>(towar, HttpStatus.OK);
    }
}
