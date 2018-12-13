package pl.michalkarwowski.api.dto;

import lombok.Builder;

@Builder
public class ErrorMessage {
    String errorField;
    String message;
}
