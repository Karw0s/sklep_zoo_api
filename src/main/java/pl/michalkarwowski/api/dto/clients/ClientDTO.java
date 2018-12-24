package pl.michalkarwowski.api.dto.clients;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import pl.michalkarwowski.api.dto.AddressDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Data
public class ClientDTO {
    @NotNull(message = "Company Name cannot be null")
    @Size(min = 2, message = "Company name should be at least 2 characters")
    private String companyName;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long nipNumber;
    @NotNull
    private AddressDTO address;
    @Size(min = 2, message = "firstName should be at least 2 characters")
    private String firstName;
    @Size(min = 2, message = "lastName should be at least 2 characters")
    private String lastName;
}
