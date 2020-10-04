package edu.wctc.registration.dto;

import edu.wctc.registration.validation.PasswordMatches;
import edu.wctc.registration.validation.WctcEmail;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@PasswordMatches
@Data
@NoArgsConstructor
public class UserDto {
    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    @WctcEmail
    @NotNull
    @NotEmpty
    private String email;
}
