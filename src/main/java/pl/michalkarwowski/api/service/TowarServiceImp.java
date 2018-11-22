package pl.michalkarwowski.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.model.Towar;
import pl.michalkarwowski.api.repository.TowarRepository;

import java.util.List;

@Service
public class TowarServiceImp implements TowarService {
    private final TowarRepository towarRepository;

    @Autowired
    public TowarServiceImp (TowarRepository towarRepository){
        this.towarRepository = towarRepository;
    }

    @Override
    public Towar addNewTowar(Towar towar) {
        return towarRepository.save(towar);
    }

    @Override
    public List<Towar> getTowary() {
        return towarRepository.findAll();
    }

    @Override
    public Towar getTowar(String name) {
        return towarRepository.findByName(name);
    }
}
