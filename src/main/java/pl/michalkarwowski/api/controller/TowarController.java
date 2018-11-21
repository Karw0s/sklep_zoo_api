package pl.michalkarwowski.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.michalkarwowski.api.model.Towar;

@RestController
public class TowarController {

    @GetMapping("/towar")
    public ResponseEntity<Towar> getTowar(){
        Towar towar = new Towar();
        return new ResponseEntity<>(towar, HttpStatus.OK);
    }
}
