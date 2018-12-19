package pl.michalkarwowski.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.michalkarwowski.api.models.AppUserDetails;
import pl.michalkarwowski.api.services.ApplicationUserService;

@RestController
public class AccountController {

    private ApplicationUserService applicationUserService;

    @Autowired
    public AccountController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    @PutMapping("/account/details")
    public ResponseEntity<AppUserDetails> updateAccountDetails(AppUserDetails appUserDetails) {
        AppUserDetails userDetails = applicationUserService.updateUserDetails(appUserDetails);
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }
}
