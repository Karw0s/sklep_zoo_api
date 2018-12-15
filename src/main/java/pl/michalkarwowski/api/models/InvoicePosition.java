package pl.michalkarwowski.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class InvoicePosition {


    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "fk_invoice")
    @JsonIgnore
    private Invoice invoice;
    @ManyToOne
    @JoinColumn(name = "fk_product")
    private Product product;
    private Integer quantity;
    private Double nettoValue;      // amount * productNettoPrice
    private Double bruttoValue;     // nettoValue + tax * nettoValue
    private Double totalTaxValue;   // tax * nettoValue
}
