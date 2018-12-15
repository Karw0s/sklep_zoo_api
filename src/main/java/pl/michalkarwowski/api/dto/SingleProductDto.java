package pl.michalkarwowski.api.dto;

import lombok.Builder;
import pl.michalkarwowski.api.model.Product;

import java.util.List;

@Builder
public class SingleProductDto {
    private Product result;
    private List<ErrorMessage> error;
}