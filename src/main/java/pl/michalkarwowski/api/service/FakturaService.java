package pl.michalkarwowski.api.service;

import pl.michalkarwowski.api.model.Faktura;
import pl.michalkarwowski.api.model.Product;

public interface FakturaService {
    Faktura addNewPosition(Product product, String idFaktura, Integer ilosc);
    Faktura getFaktura(String nrFaktury);
}
