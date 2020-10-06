package edu.wctc.registration.captcha.error;

public class ReCaptchaInvalidException extends RuntimeException {
    public ReCaptchaInvalidException(String message) {
        super(message);
    }
}
