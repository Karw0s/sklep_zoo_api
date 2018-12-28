package pl.michalkarwowski.api.dto.products;

import lombok.Data;

@Data
public class ProductDetailsDTO {

    private Integer id;
    private String name;
    private String manufacturer;
    private Double priceNetto;
    private Double priceBrutto;
    private Integer tax;
    private String unitOfMeasure;
    private String pkwiuCode;
}
