package pl.michalkarwowski.api.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.dto.AppUserDetailsDTO;
import pl.michalkarwowski.api.dto.EmailVerificationDTO;
import pl.michalkarwowski.api.models.AppUserDetails;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.services.ApplicationUserService;

import javax.validation.Valid;

@RestController
public class AccountController {

    private ApplicationUserService applicationUserService;
    private ModelMapper modelMapper;

    @Autowired
    public AccountController(ApplicationUserService applicationUserService,
                             ModelMapper modelMapper) {
        this.applicationUserService = applicationUserService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/account/details")
    public ResponseEntity<AppUserDetailsDTO> getAccountDetails() {
        AppUserDetails userDetails = applicationUserService.getUserDetails();
        return new ResponseEntity<>(modelMapper.map(userDetails, AppUserDetailsDTO.class), HttpStatus.OK);
    }

    @GetMapping("/account/verified")
    public ResponseEntity<EmailVerificationDTO> isVerified() {
        ApplicationUser user = applicationUserService.getCurrentUser();
        return new ResponseEntity<>(EmailVerificationDTO.builder()
                .verified(user.isVerified())
                .build(), HttpStatus.OK);
    }
    @PutMapping("/account/details")
    public ResponseEntity<AppUserDetailsDTO> updateAccountDetails(@Valid @RequestBody AppUserDetailsDTO appUserDetails) {
        AppUserDetails userDetails = applicationUserService.updateUserDetails(appUserDetails);
        if (appUserDetails == null) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return new ResponseEntity<>(modelMapper.map(userDetails, AppUserDetailsDTO.class), HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        if (applicationUserService.verifyUser(token)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
    }
}
