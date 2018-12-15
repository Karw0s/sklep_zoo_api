package pl.michalkarwowski.api.service;

import pl.michalkarwowski.api.model.ApplicationUser;

public interface ApplicationUserService {
    ApplicationUser getCurrentUser();
    ApplicationUser saveAppUser(ApplicationUser applicationUser);
}
