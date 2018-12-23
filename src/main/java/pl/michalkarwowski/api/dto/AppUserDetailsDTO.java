package pl.michalkarwowski.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class AppUserDetailsDTO {

    private String bank;
    private String bankAccountNumber;
    private String email;
    private String companyName;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long nipNumber;
    private AddressDTO address;
    private String firstName;
    private String lastName;
}
