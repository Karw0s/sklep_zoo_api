package pl.michalkarwowski.api.services;

import pl.michalkarwowski.api.models.ApplicationUser;

public interface ApplicationUserService {
    ApplicationUser getCurrentUser();
    ApplicationUser saveAppUser(ApplicationUser applicationUser);
}
