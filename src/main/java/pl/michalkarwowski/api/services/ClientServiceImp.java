package pl.michalkarwowski.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.dto.ClientsDetailDTO;
import pl.michalkarwowski.api.models.Address;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.models.Client;
import pl.michalkarwowski.api.repositories.ClientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImp implements ClientService {

    private final ClientRepository clientRepository;
    private final ApplicationUserService applicationUserService;
    private final AddressService addressService;

    @Autowired
    public ClientServiceImp(ClientRepository clientRepository,
                            ApplicationUserService applicationUserService,
                            AddressService addressService) {
        this.clientRepository = clientRepository;
        this.applicationUserService = applicationUserService;
        this.addressService = addressService;
    }

    @Override
    public List<ClientsDetailDTO> getUserClients() {
        List<Client> clients = applicationUserService.getCurrentUser().getClients();
        List<ClientsDetailDTO> clientsDetailList = new ArrayList<>();

        for(Client client: clients) {
            Address address = client.getAddress();
            String addressString = String.join(" ", address.getStreet(), address.getZipCode(), address.getCity());
            clientsDetailList.add(ClientsDetailDTO.builder()
                    .id(client.getId())
                    .companyName(client.getCompanyName())
                    .nipNumber(client.getNipNumber())
                    .address(addressString)
                    .build());
        }
        return clientsDetailList;
    }

    @Override
    public Client createClient(Client client) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        addressService.createAddress(client.getAddress());
        Client newClient = clientRepository.save(client);
        applicationUser.getClients().add(newClient);
        applicationUserService.saveAppUser(applicationUser);
        return newClient;
    }

    @Override
    public Client getClient(Integer id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            if (applicationUserService.getCurrentUser().getClients().contains(client.get())) {
                return client.get();
            }
        }
        return null;
    }

    @Override
    public Client updateClient(Integer id, Client client) {
        Client clientDB = getClient(id);
        boolean addressChanged = false;
        if (clientDB != null) {
            if (!clientDB.getAddress().equals(client.getAddress())) {
                this.addressService.updateAddress(client.getAddress());
                addressChanged = true;
            }
            if (!clientDB.equals(client)) {
                return clientRepository.save(client);
            }
            if (addressChanged) {
                return clientDB;
            }
        }
        return null;
    }

    @Override
    public boolean deleteClient(Integer id) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent())
            if (applicationUser.getClients().contains(client.get())) {
                if (applicationUser.getClients().remove(client.get())) {
                    applicationUserService.saveAppUser(applicationUser);
                    clientRepository.deleteById(id);
                    return true;
                }
            }
        return false;
    }
}
