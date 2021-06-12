package com.example.project1.config.emailToken.emailSender;

import com.example.project1.config.emailToken.ConfirmationToken;
import com.example.project1.config.emailToken.EmailToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    @Autowired
    private ConfirmationToken confirmationToken;

    private final JavaMailSender mailSender;

    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void deleteToken(EmailToken token){confirmationToken.delete(token);}

    public void saveToken(EmailToken token){confirmationToken.save(token);}

    public EmailToken findTokenByEmailToken(String token){return confirmationToken.findByToken(token);}

    @Async
    public void sendEmail(SimpleMailMessage email) {
        mailSender.send(email);
    }
}
