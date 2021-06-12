package com.example.project1.controller;

import com.example.project1.service.BookedInfoService;
import com.example.project1.tours.Tour;
import com.example.project1.user.PDF;
import com.example.project1.tours.TourDetails;
import com.example.project1.user.BookedInfo;
import com.example.project1.user.UserDetails;
import com.example.project1.user.Usr;
import com.example.project1.repos.UserDetailsRepository;
import com.example.project1.repos.UserRepository;
import com.example.project1.service.TourService;
import com.example.project1.service.UserService;
import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    TourService tourService;

    @Autowired
    BookedInfoService bookedInfoService;


    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("user/{username}")
    public String userInfoPage(@PathVariable String username, Model model, @AuthenticationPrincipal Usr usr,
                               UserDetails userDetails) {
        model.addAttribute("username",username);
        userDetails=userDetailsRepository.findUserDetailsByUsr(usr);
        model.addAttribute("usrdet",userDetails);
        List<BookedInfo> bookedInfo=bookedInfoService.getBookingInfo(userDetails);
        UserDetails userDetails1=new UserDetails();
        model.addAttribute("usrdet1",userDetails1);
        model.addAttribute("newusername",usr.getUsername());
        model.addAttribute("password",usr.getPassword());
        model.addAttribute("newpassword",usr.getPassword());

        if(bookedInfo!=null){
            model.addAttribute("bi", bookedInfo);
        }
        else{
            model.addAttribute("bi", null);
        }
        return "userpage";
    }

    @PostMapping("addinfo")
    public ModelAndView addUserDetails(@AuthenticationPrincipal Usr usr,
                                       @ModelAttribute("usrdet1") @Valid UserDetails userDetails,BindingResult bindingResult) {
        ModelAndView mav=new ModelAndView();
        mav.setViewName("redirect:/user/"+usr.getUsername());
        if(bindingResult.hasErrors()){
            return mav;
        }
        userService.addPersonalInfo(usr.getId(),userDetails);
        return mav;
    }

    @PostMapping("changeusername")
    public ModelAndView changeUsername(@AuthenticationPrincipal Usr usr,
                                       @ModelAttribute("newusername") @Valid String newusername,
                                       RedirectAttributes redirectAttributes) {
        ModelAndView mav=new ModelAndView();
        mav.setViewName("redirect:/user/"+usr.getUsername());
        if(userRepo.findByUsername(newusername)!=null){
            redirectAttributes.addFlashAttribute("usernameerror",true);
            return mav;
        }
        userService.updateUsername(newusername,usr.getId());
        mav.setViewName("redirect:/user/"+newusername);
        return mav;
    }

    @PostMapping("changepassword")
    public ModelAndView changePassword(@AuthenticationPrincipal Usr usr, HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {
        ModelAndView mav=new ModelAndView();
        mav.setViewName("redirect:/user/"+usr.getUsername());
        String newpassword=request.getParameter("newpassword");
        String oldpassword=request.getParameter("password");
        if(newpassword.length()<8){
            redirectAttributes.addFlashAttribute("passwordlength",true);
            return mav;
        }
        if(!userService.updatePassword(oldpassword,newpassword,usr)){
            redirectAttributes.addFlashAttribute("passworderr",true);
            return mav;
        }
        return mav;
    }

    @RequestMapping(value = "delete/{i}",method = {RequestMethod.DELETE,RequestMethod.POST})
    public ModelAndView cancelBooking(@AuthenticationPrincipal Usr usr,
                                      @PathVariable String i) {
        ModelAndView mav=new ModelAndView();
        Long id=Long.parseLong(i);
        mav.setViewName("redirect:/user/"+usr.getUsername());
        BookedInfo delBi=bookedInfoService.getBookingInfoById(id);
        TourDetails tD=tourService.findTourDetailsByTour(delBi.getTours().get(0));
        tD.setNumberofpeople(delBi.getPerson()+tD.getNumberofpeople());
        tourService.saveTourDet(tD);
        bookedInfoService.deleteBookingInfo(delBi);

        return mav;
    }

    @RequestMapping(value = "pay/{i}",method = {RequestMethod.POST})
    public ModelAndView payTour(@AuthenticationPrincipal Usr usr,
                                @PathVariable String i, RedirectAttributes attributes) throws IOException, DocumentException, MessagingException {
        ModelAndView mav=new ModelAndView();
        Long id=Long.parseLong(i);
        BookedInfo bi=bookedInfoService.getBookingInfoById(id);
        UserDetails userDetails=bi.getUserDetails();
        PDF pdf=new PDF();
        String html = pdf.parseThymeleafTemplate(userDetails,bi);
        pdf.generatePdfFromHtml(html,bi.getBookid());

        userService.sendPdf(bi,usr);
        mav.setViewName("redirect:/user/"+usr.getUsername());
        attributes.addFlashAttribute("emailcheck",true);
        return mav;

    }



}
