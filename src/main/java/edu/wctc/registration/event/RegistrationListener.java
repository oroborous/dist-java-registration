package edu.wctc.registration.event;

import edu.wctc.registration.repo.entity.User;
import edu.wctc.registration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);

        SimpleMailMessage email = constructEmailMessage(event, user, token);
        mailSender.send(email);
    }

    private SimpleMailMessage constructEmailMessage(OnRegistrationCompleteEvent event,
                                                    User user,
                                                    String token) {
        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = event.getAppUrl() + "/c/confirm?token=" + token;
        String message = "You registered successfully. To confirm your registration, please click on the below link.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        confirmRegistration(event);
    }
}
