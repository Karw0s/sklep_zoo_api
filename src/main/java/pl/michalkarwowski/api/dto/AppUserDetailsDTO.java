package pl.michalkarwowski.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AppUserDetailsDTO {

    @Size(min = 2, message = "Bank name should be at least 2 characters")
    private String bank;
    private String bankAccountNumber;
    @Email(message = "Email should be valid")
    private String email;
    @NotNull
    @Size(min = 2, message = "Company name should be at least 2 characters")
    private String companyName;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long nipNumber;
    @NotNull
    private AddressDTO address;
    private String firstName;
    private String lastName;
}
