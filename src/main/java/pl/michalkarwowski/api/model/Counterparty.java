package pl.michalkarwowski.api.model;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Counterparty extends Person {

    private Address address;
}
