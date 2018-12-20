package pl.michalkarwowski.api.services;

import pl.michalkarwowski.api.dto.AppUserRegistrationDTO;
import pl.michalkarwowski.api.models.AppUserDetails;
import pl.michalkarwowski.api.models.ApplicationUser;

import java.util.Optional;

public interface ApplicationUserService {
    ApplicationUser getCurrentUser();
    ApplicationUser saveAppUser(ApplicationUser applicationUser);
    ApplicationUser findByUsername(String username);
    ApplicationUser registerAppUser(AppUserRegistrationDTO userRegistrationDTO);
    AppUserDetails getUserDetails();
    AppUserDetails updateUserDetails(AppUserDetails appUserDetails);
}
