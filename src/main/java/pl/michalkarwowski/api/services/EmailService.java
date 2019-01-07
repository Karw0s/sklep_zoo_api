package pl.michalkarwowski.api.services;

import pl.michalkarwowski.api.models.ApplicationUser;

public interface EmailService {
    void sendVerificationEmail(ApplicationUser user);
}
