package pl.michalkarwowski.api.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.michalkarwowski.api.events.OnRegistrationCompleteEvent;
import pl.michalkarwowski.api.services.EmailService;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private EmailService emailService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        emailService.sendVerificationEmail(event.getApplicationUser());
    }
}
