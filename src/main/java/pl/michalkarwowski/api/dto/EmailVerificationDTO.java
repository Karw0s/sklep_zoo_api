package pl.michalkarwowski.api.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmailVerificationDTO {
    private boolean verified;
}
