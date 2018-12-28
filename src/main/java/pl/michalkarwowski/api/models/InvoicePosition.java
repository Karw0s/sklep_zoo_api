package pl.michalkarwowski.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class InvoicePosition {


    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "fk_product")
    private Product product;

    private Integer ordinalNumber;

    private String name;
    private String pkwiuCode;

    private Double quantity;
    private String tax;
    private Double priceNetto;
    private String unitOfMeasure;
    private Double totalPriceBrutto;
    private Double totalPriceNetto;
    private Double totalPriceTax;
}
