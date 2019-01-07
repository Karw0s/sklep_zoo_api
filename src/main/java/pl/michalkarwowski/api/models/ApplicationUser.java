package pl.michalkarwowski.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class ApplicationUser {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private boolean verified = false;

    @OneToMany
    private List<Product> products;
    @OneToMany
    private List<Client> clients;
    @OneToMany
    private List<Invoice> invoices;
    @OneToOne
    private AppUserDetails userDetails;
    @OneToMany
    private List<InvoiceNextNumber> invoiceNextNumber = new ArrayList<>();

}
