package pl.michalkarwowski.api.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class PozycjaFaktury {


    @Id
    @GeneratedValue
    private Integer lp;
    @ManyToOne
    @JoinColumn(name = "fk_faktura")
    private Faktura faktura;
    @ManyToOne
    @JoinColumn(name = "fk_towar")
    private Towar towar;
    private Integer ilosc;
}
