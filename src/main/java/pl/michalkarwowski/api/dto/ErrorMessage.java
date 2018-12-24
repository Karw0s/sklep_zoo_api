package pl.michalkarwowski.api.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorMessage {
    String errorField;
    String message;
}
