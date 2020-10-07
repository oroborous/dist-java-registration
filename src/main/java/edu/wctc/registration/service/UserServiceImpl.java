package edu.wctc.registration.service;

import edu.wctc.registration.dto.UserDto;
import edu.wctc.registration.error.UserAlreadyExistsException;
import edu.wctc.registration.repo.RoleRepository;
import edu.wctc.registration.repo.UserRepository;
import edu.wctc.registration.repo.VerificationTokenRepository;
import edu.wctc.registration.repo.entity.User;
import edu.wctc.registration.repo.entity.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "Distributed Java Demo";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createVerificationTokenForUser(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public VerificationToken generateNewVerificationToken(String existingVerificationToken) {
        VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);
        vToken.updateToken(UUID.randomUUID().toString());
        vToken = tokenRepository.save(vToken);
        return vToken;
    }

    @Override
    public String generateQRUrl(User user) {
        return QR_PREFIX + URLEncoder.encode(
                String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                        APP_NAME,
                        user.getEmail(),
                        user.getSecret(),
                        APP_NAME), StandardCharsets.UTF_8);
    }

    @Override
    public User getUser(String verificationToken) {
        VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
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

    @Override
    public User updateUser2FA(boolean use2FA) {
        // Get currently authenticated user from Spring security context
        Authentication curAuth = SecurityContextHolder.getContext().getAuthentication();

        // Extract our entity object from principal
        User currentUser = (User) curAuth.getPrincipal();
        // Update 2FA setting
        currentUser.setUsing2FA(use2FA);
        // Save to database
        currentUser = userRepository.save(currentUser);

        // Create a new authentication with updated user
        Authentication auth = new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(), curAuth.getAuthorities());
        // Set new authentication in Spring security context
        SecurityContextHolder.getContext().setAuthentication(auth);
        // Return updated user entity object
        return currentUser;
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        User user = verificationToken.getUser();

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
//            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
//        tokenRepository.delete(verificationToken);
        userRepository.save(user);
        return TOKEN_VALID;
    }
}
