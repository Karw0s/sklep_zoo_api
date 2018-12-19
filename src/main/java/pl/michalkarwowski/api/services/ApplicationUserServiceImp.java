package pl.michalkarwowski.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.dto.AppUserRegistrationDTO;
import pl.michalkarwowski.api.models.AppUserDetails;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.repositories.AppUserDetailsRepository;
import pl.michalkarwowski.api.repositories.ApplicationUserRepository;

@Service
public class ApplicationUserServiceImp implements ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;
    private final AppUserDetailsRepository appUserDetailsRepository;
    private AddressService addressService;

    @Autowired
    public ApplicationUserServiceImp(ApplicationUserRepository applicationUserRepository,
                                     AppUserDetailsRepository appUserDetailsRepository,
                                     AddressService addressService) {
        this.applicationUserRepository = applicationUserRepository;
        this.appUserDetailsRepository = appUserDetailsRepository;
        this.addressService = addressService;
    }

    @Override
    public ApplicationUser getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return applicationUserRepository.findByUsername(username);
    }

    @Override
    public ApplicationUser saveAppUser(ApplicationUser applicationUser) {
        return applicationUserRepository.save(applicationUser);
    }

    @Override
    public ApplicationUser findByUsername(String username) {
        return applicationUserRepository.findByUsername(username);
    }

    @Override
    public ApplicationUser registerAppUser(AppUserRegistrationDTO userRegistrationDTO) {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setPassword(userRegistrationDTO.getPassword());
        applicationUser.setUsername(userRegistrationDTO.getUsername());
        return applicationUserRepository.save(applicationUser);
    }

    @Override
    public AppUserDetails updateUserDetails(AppUserDetails appUserDetails) {
        ApplicationUser applicationUser = getCurrentUser();
        applicationUser.setUserDetails(appUserDetails);
        return applicationUserRepository.save(applicationUser).getUserDetails();
    }
}
