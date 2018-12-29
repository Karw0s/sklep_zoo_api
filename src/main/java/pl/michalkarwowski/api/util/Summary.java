package pl.michalkarwowski.api.util;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Summary {
    private Double totalPriceGross;
    private Double totalPriceNet;
    private Double totalPriceTax;
}
