package pl.michalkarwowski.api.service;

import pl.michalkarwowski.api.model.Towar;

import java.util.List;

public interface TowarService {
    Towar addNewTowar (Towar towar);
    List<Towar> getTowary();
    Towar getTowar(String name);
}
