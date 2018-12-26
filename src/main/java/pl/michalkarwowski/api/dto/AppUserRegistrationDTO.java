package pl.michalkarwowski.api.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AppUserRegistrationDTO {
    @NotNull
    private String username;
    @NotNull
    @Size(min = 8, max = 20, message = "password should be at least 8 char and max 20")
    private String password;
    @NotNull
    @Email(message = "Email should be valid")
    private String email;
}
