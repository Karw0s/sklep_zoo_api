package pl.michalkarwowski.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.model.ApplicationUser;
import pl.michalkarwowski.api.repository.ApplicationUserRepository;

@Service
public class ApplicationUserServiceImp implements ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;

    @Autowired
    public ApplicationUserServiceImp(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
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
}
