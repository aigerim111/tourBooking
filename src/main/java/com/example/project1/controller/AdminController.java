package com.example.project1.controller;

import com.example.project1.repos.BookingRepository;
import com.example.project1.service.BookedInfoService;
import com.example.project1.tours.Tour;
import com.example.project1.service.TourService;
import com.example.project1.tours.TourDetails;
import com.example.project1.user.BookedInfo;
import com.example.project1.user.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Controller
public class AdminController {

    @Autowired
    TourService tourService;

    @Autowired
    BookedInfoService bookedInfoService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/newTour")
    public String newTour(Model model){
        Tour tour=new Tour();
        TourDetails tourDetails=new TourDetails();
        model.addAttribute("tour",tour);
        model.addAttribute("tourdetails",tourDetails);
        model.addAttribute("desc","After adding new tour dont forget to add description!");
        model.addAttribute("tours",tourService.findAll());
        return "adminspage";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/save")
    public String saveTour(@ModelAttribute("tour") @Valid Tour tour, HttpServletRequest request, RedirectAttributes redirectAttributes) throws ParseException {
        Tour newtour=new Tour();
        String date=request.getParameter("tourdate");
        newtour.setTourdate(date);
        newtour.setTour(tour);
        tourService.saveTour(tour);
        return "redirect:/newTour";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/delete")
    public String deleteTour(@ModelAttribute("tourid") Long Id){
        tourService.deleteTour(Id);
        return "redirect:/tours";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/adddate")
    public String addDate(HttpServletRequest request) throws ParseException {
        String date=request.getParameter("updateDate");
        Long tourid=Long.parseLong(request.getParameter("tourid"));
        Tour tour=tourService.findingTourById(tourid);
        tour.setTourdate(date);
        tourService.saveTour(tour);
        return "redirect:/tours";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addDescription")
    public String descTour(@ModelAttribute("tourDetails") @Valid TourDetails tourDetails,HttpServletRequest request,Model model) throws ParseException {
        TourDetails newTourDetails=new TourDetails();
        Tour tour=tourService.findingTourById(Long.parseLong(request.getParameter("tourid")));
        newTourDetails.setTourDetails(tourDetails);
        newTourDetails.setTour(tour);
        tour.setTourDetails(newTourDetails);
        tourService.saveTourDet(newTourDetails);
        return "redirect:/newTour";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/booking/{id}")
    public ModelAndView getUsers(@PathVariable(name = "id") Long tourid,RedirectAttributes attributes){
        ModelAndView mav=new ModelAndView();
        mav.setViewName("redirect:/newTour");
        Tour tour=tourService.findingTourById(tourid);
        List<BookedInfo> bookedInfos=bookedInfoService.getBookedInfoByTour(tour);
        attributes.addFlashAttribute("tourname",tour.getTourname());
        attributes.addFlashAttribute("bi",bookedInfos);
        attributes.addFlashAttribute("showinfo",true);
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/notify/{id}")
    public ModelAndView notifyUsers(@PathVariable(name="id") Long tourid){
        tourService.sendEmailNotifications(tourService.findingTourById(tourid));
        ModelAndView mav=new ModelAndView();
        mav.setViewName("redirect:/newTour");
        return mav;
    }


}
