package pl.michalkarwowski.api.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Data
public class AddressDTO {
    @NotNull
    @Size(min = 2, message = "Street name should be at least 2 characters")
    private String street;
    @NotNull
    private String zipCode;
    @NotNull
    @Size(min = 2, message = "City should be at least 2 characters")
    private String city;
    @NotNull
    @Size(min = 2, message = "Country should be at least 2 characters")
    private String country;
}
