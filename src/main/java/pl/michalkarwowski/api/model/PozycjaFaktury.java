package pl.michalkarwowski.api.model;

import javax.persistence.*;

@Entity
public class PozycjaFaktury {

    private Integer lp;
    @OneToMany
    @JoinColumn(name = "fk_towar")
    private Towar towar;
    private Integer ilosc;
}
