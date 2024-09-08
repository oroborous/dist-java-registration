package edu.wctc.registration.dto;

import edu.wctc.registration.validation.PasswordMatches;
import edu.wctc.registration.validation.WctcEmail;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    private boolean isUsing2FA;
}
