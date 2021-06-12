package com.example.project1.service;

import com.example.project1.repos.BookingRepository;
import com.example.project1.user.BookedInfo;
import com.example.project1.user.Role;
import com.example.project1.user.UserDetails;
import com.example.project1.user.Usr;
import com.example.project1.config.emailToken.ConfirmationToken;
import com.example.project1.config.emailToken.EmailToken;
import com.example.project1.config.emailToken.emailSender.EmailSender;
import com.example.project1.repos.UserDetailsRepository;
import com.example.project1.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private EmailSender emailSender;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    TourService tourService;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Usr user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }


    public boolean addNewUser(Usr usr) {
        Usr userFromDb = userRepository.findByUsername(usr.getUsername());
        Usr emailCheck=userRepository.findUsrByEmail(usr.getEmail());

        if (userFromDb!=null) {
            return false;
        }
        else if(emailCheck!=null){
            return false;
        }


            usr.setRole(Role.USER);
            usr.setEmail(usr.getEmail());
            usr.setUsername(usr.getUsername());
            usr.setPassword(passwordEncoder.encode(usr.getPassword()));

            userRepository.save(usr);
        EmailToken token = new EmailToken(usr);

        emailSender.saveToken(token);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(usr.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("tourbooking@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                +"http://localhost:8080/confirmaccount?token="+token.getToken());

        emailSender.sendEmail(mailMessage);

        return true;
    }

    public void deleteusr(Usr usr){
        userRepository.delete(usr);
    }

    public void addPersonalInfo(Long id, com.example.project1.user.UserDetails userDetails){
        userDetails.setUsr(userRepository.findUsrById(id));
        saveUserDetails(userDetails);
    }

    public void updateUsername(String username,Long id){
        userRepository.updateUsername(username,id);
    }

    public boolean updatePassword(String oldPassword,String newPassword,Usr usr){
        if(!(passwordEncoder.matches(oldPassword,usr.getPassword()))){
            return false;
        }
        if(oldPassword.equals(newPassword)){
            return false;
        }
        usr.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(usr);
        return true;
    }

    public Usr findUsrByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public void saveUsr(Usr usr){
        userRepository.save(usr);
    }

    public void saveUserDetails(UserDetails userDetails){
        userDetailsRepository.save(userDetails);
    }

    public UserDetails findUserDetailsByUsr(Usr usr){
        return userDetailsRepository.findUserDetailsByUsr(usr);
    }

    public Usr findUsrByEmail(String email){return userRepository.findUsrByEmail(email);}

    public void forgetPassword(Usr usr){
        EmailToken token = new EmailToken(usr);

        emailSender.saveToken(token);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(usr.getEmail());
        mailMessage.setSubject("Reset password!");
        mailMessage.setFrom("tourbooking@gmail.com");
        mailMessage.setText("To reset password, follow the link : "
                +"http://localhost:8080/confirmreset?token="+token.getToken());

        emailSender.sendEmail(mailMessage);
    }

    public Usr findUsrById(Long id){
        return userRepository.findUsrById(id);
    }

    public void sendPdf(BookedInfo bi,Usr usr) throws MessagingException, FileNotFoundException {
        MimeMessage mailMessage=mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(mailMessage,true);
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("user/" + bi.getBookid()+".pdf").getFile());
        helper.setTo(usr.getEmail());
        helper.setSubject("Confirmation ticket!");
        helper.setFrom("tourbooking@gmail.com");
        helper.setText("Dear user! This is your confirmation paper!");
        helper.addAttachment(bi.getBookid()+".pdf",file);

        mailSender.send(mailMessage);

        file.delete();
    }




}
