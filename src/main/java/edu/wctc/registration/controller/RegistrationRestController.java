package edu.wctc.registration.controller;

import edu.wctc.registration.dto.GenericResponse;
import edu.wctc.registration.dto.UserDto;
import edu.wctc.registration.event.OnRegistrationCompleteEvent;
import edu.wctc.registration.repo.entity.User;
import edu.wctc.registration.repo.entity.VerificationToken;
import edu.wctc.registration.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/r")
public class RegistrationRestController {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment env;

    private SimpleMailMessage constructResendVerificationTokenEmail(String contextPath,
                                                                    VerificationToken newToken,
                                                                    User user) {
        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = contextPath + "/c/confirm?token=" + newToken;
        String message = "To confirm your registration, please click on the below link.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @PostMapping("/register")
    public GenericResponse registerUserAccount(@Valid UserDto accountDto,
                                               HttpServletRequest request) {
        log.debug("Registering user account with information: {}", accountDto);

        User registered = userService.registerNewUserAccount(accountDto);

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                request.getLocale(),
                getAppUrl(request)));
        return new GenericResponse("success");
    }

    @GetMapping("/resendToken")
    public GenericResponse resendRegistrationToken(HttpServletRequest request,
                                                   @RequestParam("token") String existingToken) {
        VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        User user = userService.getUser(newToken.getToken());
        String contextPath = getAppUrl(request);
        mailSender.send(constructResendVerificationTokenEmail(contextPath, newToken, user));
        return new GenericResponse("A new confirmation email has been sent.");
    }
}
