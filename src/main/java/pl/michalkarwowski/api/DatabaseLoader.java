package pl.michalkarwowski.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pl.michalkarwowski.api.model.User;
import pl.michalkarwowski.api.repository.UserRepository;

@Component
public class DatabaseLoader implements ApplicationRunner {
    private UserRepository users;

    @Autowired
    public DatabaseLoader(UserRepository users){
        this.users = users;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {


        users.save(new User("Reza", "Khalid",  "rezakhalid", "password", new String[] {"ROLE_USER"}));

        users.save(new User("zia", "khalid", "zia566", "123456", new String[] {"ROLE_USER", "ROLE_ADMIN"}));


    }
}
