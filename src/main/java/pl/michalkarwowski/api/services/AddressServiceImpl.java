package pl.michalkarwowski.api.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.dto.AddressDTO;
import pl.michalkarwowski.api.models.Address;
import pl.michalkarwowski.api.repositories.AddressRepository;

import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private ModelMapper modelMapper;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository,
                              ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Address getAddress(Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    @Override
    public Address createAddress(AddressDTO addressDTO) {
        Address address = modelMapper.map(addressDTO, Address.class);
        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(Address address) {
        if (address.getId() != null) {
            Optional<Address> addressDB = addressRepository.findById(address.getId());
            if (addressDB.isPresent()) {
                if (!addressDB.get().equals(address)) {
                    return addressRepository.save(address);
                }
            }
        }
        return null;
    }
}
