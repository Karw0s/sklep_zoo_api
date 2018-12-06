package pl.michalkarwowski.api.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Faktura {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String nr;
    @OneToMany(mappedBy = "faktura")
    private List<PozycjaFaktury> pozycjaFaktury = new ArrayList<>();
    private Date dataWystawienia;


}
