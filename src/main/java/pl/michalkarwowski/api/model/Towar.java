package pl.michalkarwowski.api.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Towar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String catalogNumber;
    private String name;
    private String manufacturer;
    private String unitOfMeasure;
    private Double amount;
    private Double priceNetto;
    private Double priceBrutto;
    private Integer vat;
    private String pkiwCode;
}
