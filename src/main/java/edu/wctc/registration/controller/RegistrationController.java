package edu.wctc.registration.controller;

import edu.wctc.registration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/c")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/confirm")
    public String confirmRegistration(Model model,
                                      @RequestParam("token") String token) {

        String result = userService.validateVerificationToken(token);
        switch (result) {
            case UserService.TOKEN_VALID:
                // User user = userService.getUser(token);
                // if (user.isUsing2FA()) {
                // model.addAttribute("qr", userService.generateQRUrl(user));
                // return "redirect:/qrcode.html?lang=" + locale.getLanguage();
                // }
                //authWithoutPassword(user);
                model.addAttribute("messageKey", "Your account was verified successfully. Please login to continue.");
                return "redirect:/v/login";
            case UserService.TOKEN_EXPIRED:
                model.addAttribute("messageKey", "Your registration token has expired. Please request a new token or register again.");
                model.addAttribute("expired", true);
                model.addAttribute("token", token);
                return "redirect:/v/badUser";
            default:
                model.addAttribute("messageKey", "Invalid registration token.");
                model.addAttribute("token", token);
                return "redirect:/v/badUser";
        }
    }

}

