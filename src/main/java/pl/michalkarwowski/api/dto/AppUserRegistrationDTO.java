package pl.michalkarwowski.api.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AppUserRegistrationDTO {
    @NotNull
    private String username;
    @NotNull
    private String password;
}
