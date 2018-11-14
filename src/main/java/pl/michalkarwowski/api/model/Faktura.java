package pl.michalkarwowski.api.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@Entity
public class Faktura {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String Nr;
    @OneToMany
    private ArrayList<PozycjaFaktury> pozycjaFaktury;
    private Date dataWystawienia;


}
