package pl.michalkarwowski.api.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.dto.AddressDTO;
import pl.michalkarwowski.api.dto.AppUserDetailsDTO;
import pl.michalkarwowski.api.dto.AppUserRegistrationDTO;
import pl.michalkarwowski.api.exceptions.EmailExistsException;
import pl.michalkarwowski.api.models.Address;
import pl.michalkarwowski.api.models.AppUserDetails;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.models.VerificationToken;
import pl.michalkarwowski.api.repositories.AppUserDetailsRepository;
import pl.michalkarwowski.api.repositories.ApplicationUserRepository;
import pl.michalkarwowski.api.repositories.VerificationTokenRepository;

@Service
public class ApplicationUserServiceImp implements ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;
    private final AppUserDetailsRepository appUserDetailsRepository;
    private VerificationTokenRepository tokenRepository;
    private AddressService addressService;
    private ModelMapper modelMapper;

    @Autowired
    public ApplicationUserServiceImp(ApplicationUserRepository applicationUserRepository,
                                     AppUserDetailsRepository appUserDetailsRepository,
                                     VerificationTokenRepository tokenRepository,
                                     AddressService addressService,
                                     ModelMapper modelMapper) {
        this.applicationUserRepository = applicationUserRepository;
        this.appUserDetailsRepository = appUserDetailsRepository;
        this.tokenRepository = tokenRepository;
        this.addressService = addressService;
        this.modelMapper = modelMapper;
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
    public ApplicationUser registerAppUser(AppUserRegistrationDTO userRegistrationDTO) throws EmailExistsException {

        if (emailExist(userRegistrationDTO.getEmail())) {
            throw new EmailExistsException(
                    "There is an account with that email adress: "
                            + userRegistrationDTO.getEmail());
        }

        ApplicationUser applicationUser = new ApplicationUser();
        AppUserDetails userDetails = new AppUserDetails();

        applicationUser.setPassword(userRegistrationDTO.getPassword());
        applicationUser.setUsername(userRegistrationDTO.getUsername());
        applicationUser.setEmail(userRegistrationDTO.getEmail());

        appUserDetailsRepository.save(userDetails);
        applicationUser.setUserDetails(userDetails);
        return applicationUserRepository.save(applicationUser);
    }

    private boolean emailExist(String email) {
        ApplicationUser user = applicationUserRepository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

    @Override
    public AppUserDetails getUserDetails() {
        return getCurrentUser().getUserDetails();
    }

    @Override
    public AppUserDetails updateUserDetails(AppUserDetailsDTO appUserDetailsDTO) {
        boolean addressChanged = false;
        ApplicationUser applicationUser = getCurrentUser();
        AppUserDetails appUserDetails = modelMapper.map(appUserDetailsDTO, AppUserDetails.class);

        Address address = modelMapper.map(appUserDetailsDTO.getAddress(), Address.class);
        Address addressDB = null;
        if (applicationUser.getUserDetails().getAddress() == null) {
            addressDB = addressService.createAddress(modelMapper.map(address, AddressDTO.class));
        } else {
            address.setId(applicationUser.getUserDetails().getAddress().getId());
            addressDB = addressService.updateAddress(address);
        }

        if (addressDB != null) {
            appUserDetails.setAddress(addressDB);
            addressChanged = true;
        } else {
            appUserDetails.setAddress(address);
        }

        appUserDetails.setId(applicationUser.getUserDetails().getId());

        if (!applicationUser.getUserDetails().equals(appUserDetails) || addressChanged) {
            AppUserDetails appUserDetailsDB = appUserDetailsRepository.save(appUserDetails);
            applicationUser.setUserDetails(appUserDetailsDB);
            applicationUserRepository.save(applicationUser);
            return appUserDetailsDB;
        }
        return null;
    }

    @Override
    public boolean verifyUser(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken != null) {
            ApplicationUser user = verificationToken.getUser();

            if (user != null) {
                user.setVerified(true);
                applicationUserRepository.save(user);
                tokenRepository.delete(getVerificationToken(token));
                return true;
            }
        }
        return false;
    }

    @Override
    public void createVerificationToken(ApplicationUser user, String token) {
        VerificationToken myToken = new VerificationToken();
        myToken.setUser(user);
        myToken.setToken(token);
        tokenRepository.save(myToken);
    }


    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }
}
