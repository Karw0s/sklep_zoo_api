package pl.michalkarwowski.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.models.Address;
import pl.michalkarwowski.api.repositories.AddressRepository;

import java.util.Optional;

@Service
public class AddressServiceImp implements AddressService {

    private final AddressRepository addressRepository;

    @Autowired
    public AddressServiceImp(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public Address createAddress(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(Address address) {
        if (address.getId() != null){
            Optional<Address> addressDB = this.addressRepository.findById(address.getId());
            if (addressDB.isPresent()){
                if(!addressDB.get().equals(address)) {
                    return this.addressRepository.save(address);
                }
            }
        }
        return null;
    }
}
