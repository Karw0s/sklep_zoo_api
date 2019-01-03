package pl.michalkarwowski.api.dto.products;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ProductDTO {

    private String catalogNumber;
    @NotNull
    @Size(min = 2, message = "Product name should be at least 2 characters")
    private String name;
    @Size(min = 2, message = "Manufacturer name should be at least 2 characters")
    private String manufacturer;
    @NotNull
    private String unitOfMeasure;
    @Min(value = 0, message = "amount cannot be negative")
    private Double amount;
    @NotNull
    @Min(value = 0, message = "Price cannot be negative")
    private Double priceNetto;
    @NotNull
    @Min(value = 0, message = "Price cannot be negative")
    private Double priceBrutto;
    @NotNull
    private String  tax;
    private String pkwiuCode;
    private String barCode;
}
