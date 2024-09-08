package edu.wctc.registration.event;

import edu.wctc.registration.repo.entity.User;
import org.springframework.context.ApplicationEvent;

public class OnRequestNewVerificationTokenEvent extends ApplicationEvent {
    private final String appUrl;
    private final User user;

    public OnRequestNewVerificationTokenEvent(User user, String appUrl) {
        super(user);
        this.user = user;
        this.appUrl = appUrl;
    }
}
