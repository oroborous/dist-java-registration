package edu.wctc.registration.captcha.service;

import edu.wctc.registration.captcha.CaptchaSettings;
import edu.wctc.registration.captcha.GoogleResponse;
import edu.wctc.registration.captcha.error.ReCaptchaInvalidException;
import edu.wctc.registration.captcha.error.ReCaptchaUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

@Slf4j
@Service
public class V2ReCaptchaService implements CaptchaService {
    private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    private static final String RECAPTCHA_URL_TEMPLATE = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";

    /**
     * How the heck does this even work?!
     * https://stackoverflow.com/questions/48574780/autowired-httpservletrequest-vs-passing-as-parameter-best-practice
     */
    @Autowired
    protected HttpServletRequest request;

    /**
     * A convenience object that is populated with the site key and secret
     * from the application.properties file
     */
    @Autowired
    protected CaptchaSettings captchaSettings;

    /**
     * While not strictly necessary to get the reCaptcha to function, this extra
     * security feature prevents too many attempts from the same IP.
     */
    @Autowired
    protected CaptchaAttemptService reCaptchaAttemptService;

    /**
     * Translates an HTTP response into a REST object, much like Spring does.
     * Allows us to do the same thing internally, from a service method.
     */
    @Autowired
    protected RestOperations restTemplate;

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Override
    public String getReCaptchaSecret() {
        return captchaSettings.getSecret();
    }

    /**
     * Called by Thymeleaf to build Captcha widget into template
     *
     * @return Site key (public key) for Google reCaptcha
     */
    @Override
    public String getReCaptchaSite() {
        return captchaSettings.getSite();
    }

    @Override
    public void processResponse(String response) {
        // Too many requests, or obviously bad response?
        securityCheck(response);

        // Create a URI to contact Google and verifiy the client response
        URI verifyUri = URI.create(String.format(RECAPTCHA_URL_TEMPLATE,
                getReCaptchaSecret(),
                response,
                getClientIP()));
        try {
            // Make the call and get Google's response
            GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
            log.debug("Google's response: {} ", googleResponse.toString());

            // Google disapproves
            if (!googleResponse.isSuccess()) {
                if (googleResponse.hasClientError()) {
                    // log failed attempt from this IP
                    reCaptchaAttemptService.reCaptchaFailed(getClientIP());
                }
                throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
            }
        } catch (RestClientException rce) {
            throw new ReCaptchaUnavailableException("Registration unavailable at this time.  Please try again later.", rce);
        }

        // clear failed attempts from this IP
        reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());

        // not throwing an exception is this method's way of saying "all good"
    }

    private boolean responseSanityCheck(String response) {
        // empty or contains bogus characters?
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    private void securityCheck(String response) {
        log.debug("Attempting to validate response {}", response);

        // too many attempts?
        if (reCaptchaAttemptService.isBlocked(getClientIP())) {
            throw new ReCaptchaInvalidException("Client exceeded maximum number of failed attempts");
        }

        // obviously invalid response?
        if (!responseSanityCheck(response)) {
            throw new ReCaptchaInvalidException("Response contains invalid characters");
        }
    }
}
