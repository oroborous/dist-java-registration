package edu.wctc.registration.captcha.service;

import edu.wctc.registration.captcha.error.ReCaptchaInvalidException;

public interface CaptchaService {
    void processResponse(String response) throws ReCaptchaInvalidException;

    String getReCaptchaSite();

    String getReCaptchaSecret();
}
