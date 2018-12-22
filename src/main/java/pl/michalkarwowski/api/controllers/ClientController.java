package pl.michalkarwowski.api.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.dto.clients.ClientDTO;
import pl.michalkarwowski.api.dto.ClientsDetailDTO;
import pl.michalkarwowski.api.dto.clients.ClientCreateResponseDTO;
import pl.michalkarwowski.api.models.Client;
import pl.michalkarwowski.api.services.ClientService;

import java.util.ArrayList;
import java.util.List;


@RestController
public class ClientController {

    private final ClientService clientService;
    private ModelMapper modelMapper;

    @Autowired
    public ClientController(ClientService clientService, ModelMapper modelMapper) {
        this.clientService = clientService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/clients")
    public ResponseEntity<List<ClientsDetailDTO>> getUserClients() {
        List<ClientsDetailDTO> clientsDetailDTOList = clientService.getUserClients();
//        List<ClientsDetailDTO> clientsDetailDTOList = new ArrayList<>();
//        for (Client client: clients) {
//            clientsDetailDTOList.add(modelMapper.map(client, ClientsDetailDTO.class));
//        }
        return new ResponseEntity<>(clientsDetailDTOList, HttpStatus.OK);
    }

    @PostMapping("/clients")
    public ResponseEntity<ClientCreateResponseDTO> createClient(@RequestBody ClientDTO client) {
        Client newClient = clientService.createClient(client);
        ClientCreateResponseDTO responseDTO = modelMapper.map(newClient, ClientCreateResponseDTO.class);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable Integer id) {
        Client client = clientService.getClient(id);
        ClientDTO clientDTO = modelMapper.map(client, ClientDTO.class);
        return new ResponseEntity<>(clientDTO, HttpStatus.OK);
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Integer id,
                                               @RequestBody ClientDTO client) {
        Client updatedClient = clientService.updateClient(id, client);
        if (updatedClient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return new ResponseEntity<>(modelMapper.map(updatedClient, ClientDTO.class), HttpStatus.OK);
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Integer id) {
        if(clientService.deleteClient(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
