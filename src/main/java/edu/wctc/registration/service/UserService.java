package edu.wctc.registration.service;

import edu.wctc.registration.dto.UserDto;
import edu.wctc.registration.error.UserAlreadyExistsException;
import edu.wctc.registration.repo.entity.User;
import edu.wctc.registration.repo.entity.VerificationToken;

public interface UserService {
    String TOKEN_INVALID = "invalidToken";
    String TOKEN_EXPIRED = "expired";
    String TOKEN_VALID = "valid";

    void createVerificationTokenForUser(User user, String token);

    VerificationToken generateNewVerificationToken(String token);

    String generateQRUrl(User user);

    User getUser(String verificationToken);

    User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException;

    void saveRegisteredUser(User user);

    User updateUser2FA(boolean use2FA);

    String validateVerificationToken(String token);
}
