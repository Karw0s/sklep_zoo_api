package pl.michalkarwowski.api.dto.clients;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import pl.michalkarwowski.api.models.Address;

import javax.persistence.OneToOne;

@Builder
@Data
public class ClientsDetailDTO {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Integer id;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String companyName;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long nipNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String address;
}
