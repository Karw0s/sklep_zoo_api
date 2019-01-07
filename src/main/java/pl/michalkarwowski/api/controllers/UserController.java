package pl.michalkarwowski.api.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.dto.AppUserDetailsDTO;
import pl.michalkarwowski.api.dto.AppUserRegistrationDTO;
import pl.michalkarwowski.api.dto.ErrorMessage;
import pl.michalkarwowski.api.events.OnRegistrationCompleteEvent;
import pl.michalkarwowski.api.exceptions.EmailExistsException;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.services.ApplicationUserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private ApplicationUserService applicationUserService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserController(ApplicationUserService applicationUserService,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          ApplicationEventPublisher eventPublisher) {
        this.applicationUserService = applicationUserService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody AppUserRegistrationDTO userDto) throws JsonProcessingException {
        ApplicationUser user2 = applicationUserService.findByUsername(userDto.getUsername());
        if (user2 == null) {
            userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            ApplicationUser appUser = null;
            try {
                appUser = applicationUserService.registerAppUser(userDto);
            } catch (EmailExistsException e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                        .body(ErrorMessage.builder()
                                .message(e.getMessage())
                                .errorField("Email")
                                .build());
            }
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(SecurityContextHolder.getContext(), appUser));
            return new ResponseEntity<>(new ObjectMapper().writeValueAsString("User registered successfully"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ObjectMapper().writeValueAsString("Użytkownik istnieje"), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
