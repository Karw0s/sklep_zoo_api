package pl.michalkarwowski.api.model;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Address {

    private String street;
    private String homeNumber;
    private String zipCode;
    private String city;
    private String province;    //województwo

}
