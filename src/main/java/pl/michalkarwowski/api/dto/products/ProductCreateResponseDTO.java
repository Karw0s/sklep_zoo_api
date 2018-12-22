package pl.michalkarwowski.api.dto.products;

import lombok.Data;

@Data
public class ProductCreateResponseDTO {

    private Integer id;
    private String catalogNumber;
    private String name;
    private String manufacturer;
    private String unitOfMeasure;
    private Double amount;
    private Double priceNetto;
    private Double priceBrutto;
    private Integer tax;
    private String pkiwCode;
}
