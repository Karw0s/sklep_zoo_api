package pl.michalkarwowski.api.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Data
@Entity
public class Person {
    @Id
    @GeneratedValue
    private Integer id;
    private String firstName;
    private String lastName;
    @OneToOne
    private Address address;
    private Integer nipNumber;


}
