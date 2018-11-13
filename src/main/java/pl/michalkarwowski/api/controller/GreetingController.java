package pl.michalkarwowski.api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.michalkarwowski.api.model.Message;

@RestController
public class GreetingController {

    @RequestMapping("/")
    @CrossOrigin(origins="*", maxAge=3600)
    public Message home() {
        return new Message("Hello World");
    }

}
