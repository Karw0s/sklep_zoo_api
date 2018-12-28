package pl.michalkarwowski.api.dto.invoice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceNextNumberDTO {
    private String number;
}
