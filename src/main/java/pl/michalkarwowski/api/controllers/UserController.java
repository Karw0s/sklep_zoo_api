package pl.michalkarwowski.api.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.dto.AppUserDetailsDTO;
import pl.michalkarwowski.api.dto.AppUserRegistrationDTO;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.services.ApplicationUserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private ApplicationUserService applicationUserService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(ApplicationUserService applicationUserService,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.applicationUserService = applicationUserService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody AppUserRegistrationDTO user) throws JsonProcessingException {
        ApplicationUser user2 = applicationUserService.findByUsername(user.getUsername());
        if (user2 == null) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            applicationUserService.registerAppUser(user);
            return new ResponseEntity<>(new ObjectMapper().writeValueAsString("User registered successfully"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ObjectMapper().writeValueAsString("Użytkownik istnieje"), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
