package pl.michalkarwowski.api.services;

import pl.michalkarwowski.api.dto.ClientsDetailDTO;
import pl.michalkarwowski.api.models.Client;

import java.util.List;

public interface ClientService {
    List<ClientsDetailDTO> getUserClients();
    Client createClient(Client client);
    Client getClient(Integer id);
    Client updateClient(Integer id, Client client);
    boolean deleteClient(Integer id);
}
