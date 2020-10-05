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

    User getUser(String verificationToken);

    User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException;

    VerificationToken generateNewVerificationToken(String token);

    void saveRegisteredUser(User user);

    String validateVerificationToken(String token);
}
