package pl.michalkarwowski.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationDto {
    String username;
    String token;
    String exp;

}
