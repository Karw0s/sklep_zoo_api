package pl.michalkarwowski.api.service;

import pl.michalkarwowski.api.model.Client;

import java.util.List;

public interface ClientService {
    List<Client> getUserClients();
    Client createClient(Client client);
    Client getClient(Integer id);
    Client updateClient(Integer id, Client client);
    boolean deleteClient(Integer id);
}
