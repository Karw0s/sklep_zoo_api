package pl.michalkarwowski.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.model.Faktura;
import pl.michalkarwowski.api.model.PozycjaFaktury;
import pl.michalkarwowski.api.model.Towar;
import pl.michalkarwowski.api.repository.FakturaRepositiory;
import pl.michalkarwowski.api.repository.PozycjaFakturyRepository;

@Service
public class FakturaServiceImp implements FakturaService {

    private final FakturaRepositiory fakturaRepositiory;
    private final PozycjaFakturyRepository pozycjaFakturyRepository;

    @Autowired
    public FakturaServiceImp(FakturaRepositiory fakturaRepositiory, PozycjaFakturyRepository pozycjaFakturyRepository) {
        this.fakturaRepositiory = fakturaRepositiory;
        this.pozycjaFakturyRepository = pozycjaFakturyRepository;
    }

    @Override
    public Faktura addNewPosition(Towar towar, String idFaktura, Integer ilosc) {
        PozycjaFaktury pozycjaFaktury = new PozycjaFaktury();
        pozycjaFaktury.setFaktura(fakturaRepositiory.findByNr(idFaktura));
        pozycjaFaktury.setTowar(towar);
        pozycjaFaktury.setIlosc(ilosc);
        pozycjaFakturyRepository.save(pozycjaFaktury);
        return fakturaRepositiory.findByNr(idFaktura);
    }
}