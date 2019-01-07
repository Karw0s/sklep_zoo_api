package pl.michalkarwowski.api.services;

import pl.michalkarwowski.api.dto.AppUserDetailsDTO;
import pl.michalkarwowski.api.dto.AppUserRegistrationDTO;
import pl.michalkarwowski.api.exceptions.EmailExistsException;
import pl.michalkarwowski.api.models.AppUserDetails;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.models.VerificationToken;

public interface ApplicationUserService {
    ApplicationUser getCurrentUser();
    ApplicationUser saveAppUser(ApplicationUser applicationUser);
    ApplicationUser findByUsername(String username);
    ApplicationUser registerAppUser(AppUserRegistrationDTO userRegistrationDTO) throws EmailExistsException;
    AppUserDetails getUserDetails();
    AppUserDetails updateUserDetails(AppUserDetailsDTO appUserDetails);
    void createVerificationToken(ApplicationUser user, String token);
    ApplicationUser getUser(String verificationToken);
    VerificationToken getVerificationToken(String VerificationToken);
}
