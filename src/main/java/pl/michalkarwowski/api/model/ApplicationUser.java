package pl.michalkarwowski.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
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

    private String firstName;
    private String lastName;
    @OneToMany
    private List<Product> products;
    @OneToMany
    private List<Counterparty> counterparties;
    @OneToMany
    private List<Invoice> invoices;


}
