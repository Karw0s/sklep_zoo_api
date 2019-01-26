package pl.michalkarwowski.api.dto.users;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AppUserRegistrationDTO {
    @NotNull
    private String username;
    @NotNull
    @Size(min = 8, max = 20, message = "password should be at least 8 char and max 20")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}", message = "Password should have at least one lowercase character, uppercase character, number and one special character")
    private String password;
    @NotNull
    @Email(message = "Email should be valid")
    private String email;
}
