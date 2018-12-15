package pl.michalkarwowski.api.models;

import javax.persistence.*;

@MappedSuperclass
public class Person {

    private String companyName;
    private Integer nipNumber;
    @OneToOne
    private Address address;

    private String firstName;
    private String lastName;


}
