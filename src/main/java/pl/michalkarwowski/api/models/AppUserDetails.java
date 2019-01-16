package pl.michalkarwowski.api.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
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
