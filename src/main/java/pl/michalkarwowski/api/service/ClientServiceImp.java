package pl.michalkarwowski.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.model.ApplicationUser;
import pl.michalkarwowski.api.model.Client;
import pl.michalkarwowski.api.repository.ClientRepository;

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
    public List<Client> getUserClients() {
        return applicationUserService.getCurrentUser().getClients();
    }

    @Override
    public Client createClient(Client client) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        addressService.createAddress(client.getAddress());
        Client newClient = clientRepository.save(client);
        applicationUser.getClients().add(newClient);
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
        if (clientDB != null) {
            if (!clientDB.equals(client)) {
                return clientRepository.save(client);
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
