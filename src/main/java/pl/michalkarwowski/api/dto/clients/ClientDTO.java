package pl.michalkarwowski.api.dto.clients;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import pl.michalkarwowski.api.dto.AddressDTO;

@Builder
@Data
public class ClientDTO {
    private String companyName;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long nipNumber;
    private AddressDTO address;
    private String firstName;
    private String lastName;
}
