package edu.wctc.registration.service;

import edu.wctc.registration.dto.UserDto;
import edu.wctc.registration.error.UserAlreadyExistsException;
import edu.wctc.registration.repo.RoleRepository;
import edu.wctc.registration.repo.UserRepository;
import edu.wctc.registration.repo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }


    @Override
    public User registerNewUserAccount(UserDto userDto)
            throws UserAlreadyExistsException {

        if (emailExists(userDto.getEmail())) {
            throw new UserAlreadyExistsException(
                    "There is an account with that email address: "
                            + userDto.getEmail());
        }

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setUsing2FA(userDto.isUsing2FA());
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
        return userRepository.save(user);
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }
}
