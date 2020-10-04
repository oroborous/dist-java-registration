package edu.wctc.registration.service;

import edu.wctc.registration.dto.UserDto;
import edu.wctc.registration.error.UserAlreadyExistsException;
import edu.wctc.registration.repo.entity.User;

public interface UserService {


    User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException;

    void saveRegisteredUser(User user);


}
