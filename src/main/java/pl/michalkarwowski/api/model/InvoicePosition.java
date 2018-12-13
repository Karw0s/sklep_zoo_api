package pl.michalkarwowski.api.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class InvoicePosition {


    @Id
    @GeneratedValue
    private Integer lp;
    @ManyToOne
    @JoinColumn(name = "fk_invoice")
    private Invoice invoice;
    @ManyToOne
    @JoinColumn(name = "fk_product")
    private Product product;
    private Integer quantity;
    private Double nettoValue;      // amount * productNettoPrice
    private Double bruttoValue;     // nettoValue + tax * nettoValue
    private Double totalTaxValue;   // tax * nettoValue
}
