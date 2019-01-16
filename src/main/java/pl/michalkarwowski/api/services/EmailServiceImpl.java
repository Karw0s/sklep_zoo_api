package pl.michalkarwowski.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.models.ApplicationUser;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService {

    private final String BASE_URL = "http://localhost:4200";
    private String mailForm = "projekt.awps@gmail.com";

    private JavaMailSender emailSender;
    private ApplicationUserService userService;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender,
                            ApplicationUserService userService) {
        this.emailSender = emailSender;
        this.userService = userService;
    }

    @Override
    public void sendVerificationEmail(ApplicationUser user) {
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(mailForm);
            helper.setTo(user.getEmail());
            helper.setSubject("Verify Email AWPS");
            helper.setText("Zweryfikuj ten adres email za pomocą tego linku " + BASE_URL + "/verify?token=" + token);
            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
