package pl.michalkarwowski.api.service;

import pl.michalkarwowski.api.model.Faktura;
import pl.michalkarwowski.api.model.Towar;

public interface FakturaService {
    Faktura addNewPosition(Towar towar, String idFaktura, Integer ilosc);
}
