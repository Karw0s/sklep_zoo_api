package pl.michalkarwowski.api.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String catalogNumber;
    private String name;
    private String manufacturer;
    private String unitOfMeasure;
    private Double amount;
    @Column(precision=10, scale=2)
    private Double priceNetto;
    @Column(precision=10, scale=2)
    private Double priceBrutto;
    private String tax;
    private String pkwiuCode;
    private String barCode;
}
