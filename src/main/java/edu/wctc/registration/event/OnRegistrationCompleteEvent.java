package edu.wctc.registration.event;

import edu.wctc.registration.repo.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private final String appUrl;
    private final Locale locale;
    private final User user;

    public OnRegistrationCompleteEvent(User user, Locale locale, String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}
