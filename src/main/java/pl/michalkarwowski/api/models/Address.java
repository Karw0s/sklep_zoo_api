package pl.michalkarwowski.api.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Address {

    @Id
    @GeneratedValue
    Long id;
    private String street;
    private String zipCode;
    private String city;
    private String country;

}
