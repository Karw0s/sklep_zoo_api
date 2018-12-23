package pl.michalkarwowski.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.dto.AppUserDetailsDTO;
import pl.michalkarwowski.api.dto.AppUserRegistrationDTO;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.services.ApplicationUserService;

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
    public ResponseEntity<String> signUp(@RequestBody AppUserRegistrationDTO user) {
        ApplicationUser user2= applicationUserService.findByUsername(user.getUsername());
        if(user2 == null ){
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            applicationUserService.registerAppUser(user);
            return new ResponseEntity<>("user registered successfully", HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>("User exists",HttpStatus.OK);
        }
    }
}
