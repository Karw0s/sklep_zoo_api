package pl.michalkarwowski.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.michalkarwowski.api.repository.UserRepository;
import pl.michalkarwowski.api.model.ApplicationUser;

@Controller
@RequestMapping(path="/demo")
public class MainController {
    @Autowired
    private UserRepository userRepository;

//    public @ResponseBody String addNewUser(@RequestParam String name, @RequestParam String email){
//
//        ApplicationUser n = new ApplicationUser();
//        n.setName(name);
//        n.setEmail(email);
//        userRepository.save(n);
//        return "Saved";
//    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<ApplicationUser> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }
}
