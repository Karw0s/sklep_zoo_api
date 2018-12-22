package pl.michalkarwowski.api.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AddressDTO {
    private String street;
    private String zipCode;
    private String city;
    private String country;
}
