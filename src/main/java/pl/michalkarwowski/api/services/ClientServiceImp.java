package pl.michalkarwowski.api.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.dto.AddressDTO;
import pl.michalkarwowski.api.dto.clients.ClientDTO;
import pl.michalkarwowski.api.dto.clients.ClientsDetailDTO;
import pl.michalkarwowski.api.models.Address;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.models.Client;
import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.repositories.ClientRepository;
import pl.michalkarwowski.api.repositories.InvoiceRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImp implements ClientService {

    private final ClientRepository clientRepository;
    private InvoiceRepository invoiceRepository;
    private final ApplicationUserService applicationUserService;
    private final AddressService addressService;
    private ModelMapper modelMapper;

    @Autowired
    public ClientServiceImp(ClientRepository clientRepository,
                            InvoiceRepository invoiceRepository,
                            ApplicationUserService applicationUserService,
                            AddressService addressService,
                            ModelMapper modelMapper) {
        this.clientRepository = clientRepository;
        this.invoiceRepository = invoiceRepository;
        this.applicationUserService = applicationUserService;
        this.addressService = addressService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ClientsDetailDTO> getUserClients() {
        List<Client> clients = applicationUserService.getCurrentUser().getClients();
        List<ClientsDetailDTO> clientsDetailList = new ArrayList<>();

        for(Client client: clients) {
            Address address = client.getAddress();
            String addressString = String.join(" ", address.getStreet() + ",", address.getZipCode(), address.getCity());
            clientsDetailList.add(ClientsDetailDTO.builder()
                    .id(client.getId())
                    .companyName(client.getCompanyName())
                    .nipNumber(client.getNipNumber())
                    .address(addressString)
                    .build());
        }

        clientsDetailList.sort(Comparator.comparing(ClientsDetailDTO::getCompanyName));
        return clientsDetailList;
    }

    @Override
    public Client createClient(ClientDTO clientDTO) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        Address address = addressService.createAddress(clientDTO.getAddress());
        Client client = modelMapper.map(clientDTO, Client.class);

        client.setAddress(address);
        client = clientRepository.save(client);
        applicationUser.getClients().add(client);
        applicationUserService.saveAppUser(applicationUser);
        return client;
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
    public Client updateClient(Integer id, ClientDTO client) {
        Client clientDB = getClient(id);
        boolean addressChanged = false;

        if (clientDB != null) {
            Client client1 = modelMapper.map(client, Client.class);
            Address address = modelMapper.map(client.getAddress(), Address.class);
            Long clientAddressID = clientDB.getAddress().getId();
            Address addressDB = addressService.getAddress(clientAddressID);

            if (!modelMapper.map(addressDB, AddressDTO.class).equals(client.getAddress())) {
                address.setId(clientAddressID);
                client1.setAddress(addressService.updateAddress(address));
                addressChanged = true;
            }
            if (!modelMapper.map(clientDB, ClientDTO.class).equals(client)) {

                client1.setId(clientDB.getId());
                if (!addressChanged) {
                    client1.setAddress(addressDB);
                }

                return clientRepository.save(client1);
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
                List<Invoice> invoiceOptional = invoiceRepository.findAllByBuyerId(id);
                if (applicationUser.getClients().remove(client.get())) {
                    applicationUserService.saveAppUser(applicationUser);
                    if(invoiceOptional.isEmpty())
                        clientRepository.deleteById(id);
                    return true;
                }
            }
        return false;
    }

    @Override
    public Client createCopy(Integer id) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        Client client = applicationUser.getClients().stream().filter(c -> c.getId().equals(id)).findAny().orElse(null);
        if (client != null){
            Address address = addressService.createAddress(modelMapper.map(client.getAddress(), AddressDTO.class));
            Client clientCopy = modelMapper.map(modelMapper.map(client, ClientDTO.class), Client.class);
            clientCopy.setAddress(address);
            clientCopy = clientRepository.save(clientCopy);
            return clientCopy;
        }

        return null;
    }
}
