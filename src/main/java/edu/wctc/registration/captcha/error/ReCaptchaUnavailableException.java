package edu.wctc.registration.captcha.error;

public final class ReCaptchaUnavailableException extends RuntimeException {
    public ReCaptchaUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
