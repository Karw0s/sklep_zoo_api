package pl.michalkarwowski.api.services;

import pl.michalkarwowski.api.dto.AddressDTO;
import pl.michalkarwowski.api.models.Address;

public interface AddressService {
    Address getAddress(Long id);
    Address createAddress(AddressDTO address);
    Address updateAddress(Address address);
}
