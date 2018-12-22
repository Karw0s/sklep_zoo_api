package pl.michalkarwowski.api.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class AppUserDetails extends Client {

    private String bank;
    private String bankAccountNumber;
    @Email
    private String email;

}
