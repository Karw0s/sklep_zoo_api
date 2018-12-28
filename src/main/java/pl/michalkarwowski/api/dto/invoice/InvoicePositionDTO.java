package pl.michalkarwowski.api.dto.invoice;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InvoicePositionDTO {

    private Long id;
    private Integer invoiceId;
    private Integer productId;

    private String pkwiuCode;

    @NotNull
    private Integer ordinalNumber;

    @NotNull
    private String name;
    @NotNull
    private String tax;
    @NotNull
    private String unitOfMeasure;
    @NotNull
    private Double quantity;
    @NotNull
    private Double priceNetto;
    @NotNull
    private Double totalPriceBrutto;
    @NotNull
    private Double totalPriceNetto;
    @NotNull
    private Double totalPriceTax;
}
