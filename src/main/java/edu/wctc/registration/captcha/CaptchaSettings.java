package edu.wctc.registration.captcha;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
public class CaptchaSettings {
    /**
     * Autofilled with properties that match prefix + fieldName
     * e.g. google.recaptcha.key.site
     */
    private String site;
    private String secret;
}
