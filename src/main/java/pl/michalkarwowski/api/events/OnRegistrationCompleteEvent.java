package pl.michalkarwowski.api.events;


import org.springframework.context.ApplicationEvent;
import pl.michalkarwowski.api.models.ApplicationUser;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private ApplicationUser applicationUser;

    public OnRegistrationCompleteEvent(Object source, ApplicationUser applicationUser) {
        super(source);
        this.applicationUser = applicationUser;
    }

    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }

    public void setApplicationUser(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }
}
