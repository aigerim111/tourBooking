package com.example.project1.controller;

import com.example.project1.config.emailToken.emailSender.EmailSender;
import com.example.project1.service.BookedInfoService;
import com.example.project1.tours.Tour;
import com.example.project1.tours.TourDetails;
import com.example.project1.user.BookedInfo;
import com.example.project1.user.UserDetails;
import com.example.project1.user.Usr;
import com.example.project1.service.TourService;
import com.example.project1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    TourService tourService;

    @Autowired
    UserService userService;

    @Autowired
    BookedInfoService bookedInfoService;

    @Autowired
    EmailSender emailSender;

    @GetMapping("main")
    public String main(Model model,@AuthenticationPrincipal Usr usr){
        List<Tour> tours=tourService.findAll();
        for(int i=0;i<tours.size();i++){
            tours.get(i).setActive();
            tourService.saveTour(tours.get(i));
        }
        tourService.DeleteExpiredTour(tours);
        model.addAttribute("usr",usr);
        model.addAttribute("relevance",bookedInfoService.tourRelevance());
        model.addAttribute("tours",tours);
        return "index";
    }

    @GetMapping("tours")
    String tourPage(Model model,@AuthenticationPrincipal Usr usr){
        model.addAttribute("usr",usr);
        return tours(model,1,"address",usr);
    }


    @GetMapping("tours/{page}")
    public String tours(Model model,
                        @PathVariable(value="page") int page,
                        @Param("option") String option,
                        @AuthenticationPrincipal Usr usr
                        ){
        Page<Tour> pages=tourService.paginationSort(page,6,Sort.by(option).ascending());
        List<Tour> tours=pages.getContent();
        model.addAttribute("usr",usr);
        model.addAttribute("tours",tours);
        model.addAttribute("pages",pages);
        model.addAttribute("current",page);
        model.addAttribute("option",option);
        return "tours";
    }

    @GetMapping("tourpage/{tourid}")
    public String tourDetails(Model model, @PathVariable(value="tourid") Long tourid,@AuthenticationPrincipal Usr usr){
        model.addAttribute("usr",usr);
        Tour selTour=tourService.findingTourById(tourid);
        TourDetails tourDetails=tourService.findTourDetailsByTour(selTour);
        model.addAttribute("tours",selTour);
        model.addAttribute("tourdet",tourDetails);
       return "tourpage";
    }

    @PostMapping("/booktour/{tourid}")
    public String bookingTour(HttpServletRequest request, @AuthenticationPrincipal Usr usr, Model model,
                              @PathVariable(value = "tourid") Long tourid
                               ) throws ParseException {
        if(usr==null){
            model.addAttribute("auth",true);
        }
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("tourdate"));
        Integer numpeople = Integer.parseInt(request.getParameter("numpeople"));
        Tour selTour = tourService.findingTourById(tourid);
        TourDetails selTourDet = tourService.findTourDetailsByTour(selTour);
        UserDetails userDetails = userService.findUserDetailsByUsr(usr);
        if (numpeople == selTourDet.getNumberofpeople()) {
            selTour.setnonActive();
        }
        if (numpeople > selTourDet.getNumberofpeople()) {
            model.addAttribute("numpeoplemax", true);
            model.addAttribute("usr", usr);
            return "confirmBooking";
        }
        if(userDetails==null){
            model.addAttribute("userdeterror", true);
            model.addAttribute("usr", usr);
            return "confirmbooking";
        }
        if (bookedInfoService.getSelDateList(selTour.getTourid()).contains(date)) {
            model.addAttribute("dateissue", true);
            model.addAttribute("usr", usr);
            return "confirmBooking";
        }
        BookedInfo bookedInfo1 = new BookedInfo();
        bookedInfo1.setBookingInfo(date, userDetails, selTour, numpeople);
        bookedInfoService.saveBookingInfo(bookedInfo1);

        selTourDet.setNumberofpeople(selTourDet.getNumberofpeople()-numpeople);
        tourService.saveTourDet(selTourDet);

        model.addAttribute("successfullyadded", true);
        model.addAttribute("usr", usr);
        return "confirmBooking";
        }
}