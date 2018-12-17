package pl.michalkarwowski.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.dto.ClientsDetailDTO;
import pl.michalkarwowski.api.models.Client;
import pl.michalkarwowski.api.services.ClientService;

import java.util.List;


@RestController
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/clients")
    public ResponseEntity<List<ClientsDetailDTO>> getUserClients() {
        return new ResponseEntity<>(clientService.getUserClients(), HttpStatus.OK);
    }

    @PostMapping("/clients")
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client newClient = clientService.createClient(client);
        return new ResponseEntity<>(newClient, HttpStatus.OK);
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Integer id) {
        Client client = clientService.getClient(id);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Integer id,
                                               @RequestBody Client client) {
        Client updatedClient = clientService.updateClient(id, client);
        if (updatedClient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Integer id) {
        if(clientService.deleteClient(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
