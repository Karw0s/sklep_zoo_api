package pl.michalkarwowski.api.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.michalkarwowski.api.dto.AppUserDetailsDTO;
import pl.michalkarwowski.api.models.AppUserDetails;
import pl.michalkarwowski.api.services.ApplicationUserService;

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

    @PutMapping("/account/details")
    public ResponseEntity<AppUserDetails> updateAccountDetails(@RequestBody AppUserDetails appUserDetails) {
        AppUserDetails userDetails = applicationUserService.updateUserDetails(appUserDetails);
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }
}
