package com.example.project1.controller;

import com.example.project1.config.emailToken.emailSender.EmailSender;
import com.example.project1.user.Usr;
import com.example.project1.config.emailToken.ConfirmationToken;
import com.example.project1.config.emailToken.EmailToken;
import com.example.project1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.transform.impl.UndeclaredThrowableStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/")
public class RegistrationController {

    @Autowired
    public UserService userService;

    @Autowired
    public EmailSender emailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("login")
    public String log(){
        return "login";
    }

    @GetMapping("registration")
    public String addUser(WebRequest request, Model model) throws IllegalAccessException {
        Usr usr = new Usr();
        model.addAttribute("usr", usr);
        return "registration";
    }

    @PostMapping("registration")
    public String saveUser(@ModelAttribute("usr") @Valid Usr usr, BindingResult result, Model model)  {
        if(result.hasErrors()){
            return "registration";
        }
        if(usr.getPassword().length()<8){
            model.addAttribute("passwordlength",true);
            return "registration";
        }
        if(!userService.addNewUser(usr)){
            model.addAttribute("check",true);
            return "registration";
        }

        return "redirect:/confirmpage";
    }

    @GetMapping("confirmpage")
    public String confirmPage(){
        return "confirmationPage";
    }

    @RequestMapping(value="confirmaccount", method= {RequestMethod.GET, RequestMethod.POST})
    public String confirmUserAccount(Model model, @RequestParam("token")String emailToken){

        EmailToken token = emailSender.findTokenByEmailToken(emailToken);
        if(token==null){
            model.addAttribute("message",true);
            return "confirm";
        }
        if(token.getExpiryDate().isBefore(LocalDateTime.now())){
            model.addAttribute("time",true);
            userService.deleteusr(token.getUsr());
            emailSender.deleteToken(token);
            return "confirm";
        }
        else if(token != null)
        {
            Usr usr = userService.findUsrByUsername(token.getUsr().getUsername());
            usr.setEnabled(true);
            userService.saveUsr(usr);
            return "redirect:/login";
        }
        else
        {
            model.addAttribute("message",true);
            return "confirm";
        }
    }

    @GetMapping("forgotPassword")
    public String forgotPassword(Model model,String email){
        model.addAttribute("email",email);
        return "forgotPassword";
    }

    @RequestMapping(value = "forget", method= {RequestMethod.GET, RequestMethod.POST})
    public String forget(@ModelAttribute("email")String email, RedirectAttributes attributes){
       Usr usr=userService.findUsrByEmail(email);
       if(usr!=null){
           userService.forgetPassword(usr);
           attributes.addFlashAttribute("emailcheck",true);
           return "redirect:/forgotPassword";
       }
       else{
           attributes.addFlashAttribute("error",true);
           return "redirect:/forgotPassword";
       }
    }

    @RequestMapping(value="confirmreset", method= {RequestMethod.GET})
    public String confirmResetPassword(Model model, @RequestParam("token")String emailToken,RedirectAttributes attributes){

        EmailToken token = emailSender.findTokenByEmailToken(emailToken);
        if(token==null){
            attributes.addFlashAttribute("message",true);
            return "redirect:/forgotpassword";
        }
        else if(token.getExpiryDate().isBefore(LocalDateTime.now())){
            attributes.addFlashAttribute("time",true);
            emailSender.deleteToken(token);
            return "redirect:/forgotpassword";
        }
        else if(token != null)
        {
            Usr usr = token.getUsr();
            model.addAttribute("token",emailToken);
            return "newPassword";
        }
        else
        {
            attributes.addFlashAttribute("message",true);
            return "redirect:/forgotpassword";
        }
    }

    @RequestMapping(value = "updatePassword", method = {RequestMethod.GET,RequestMethod.POST})
    public String newPassword(HttpServletRequest request, @RequestParam("token")String emailToken ){
        EmailToken token = emailSender.findTokenByEmailToken(emailToken);
        Usr usr = token.getUsr();
        String password=request.getParameter("newpassword1");
        usr.setPassword(passwordEncoder.encode(password));
        userService.saveUsr(usr);
        return "redirect:/login";
    }




}
