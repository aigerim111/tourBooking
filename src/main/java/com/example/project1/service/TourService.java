package com.example.project1.service;

import com.example.project1.config.emailToken.EmailToken;
import com.example.project1.config.emailToken.emailSender.EmailSender;
import com.example.project1.tours.Tour;
import com.example.project1.tours.TourDetails;
import com.example.project1.repos.TourDetailsRepository;
import com.example.project1.repos.TourRepository;
import com.example.project1.user.BookedInfo;
import com.example.project1.user.UserDetails;
import com.example.project1.user.Usr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class TourService {

    @Autowired
    TourRepository tourRepo;

    @Autowired
    TourDetailsRepository tourDetailsRepository;

    @Autowired
    BookedInfoService bookedInfoService;

    @Autowired
    EmailSender sender;

    public Page<Tour> paginationSort(int page, int size, Sort sort){
        return tourRepo.findAll(PageRequest.of(page-1,size,sort));
    }

    public List<Tour> findAll(){
        return tourRepo.findAll();
    }

    public Tour findingTourById(Long Id){
        return tourRepo.findTourByTourid(Id);
    }

    public void saveTour(Tour tour){
        tourRepo.save(tour);
    }

    public TourDetails findTourDetailsByTour(Tour tour){
        return tourDetailsRepository.findTourDetailsByTour(tour);
    }

    public void deleteTour(Long Id){
        Tour deleteTour=tourRepo.findTourByTourid(Id);
        tourRepo.delete(deleteTour);
    }

    public void deleteTourDetails(TourDetails tourDetails){
        tourDetailsRepository.delete(tourDetails);
    }


    public void DeleteExpiredTour(List<Tour> tours){
        for(int i=0;i<tours.size();i++){
            if(!tours.get(i).isActive()){
                tourRepo.delete(tours.get(i));
            }
        }
    }

    public void saveTourDet(TourDetails tourDetails){
        tourDetailsRepository.save(tourDetails);
    }

    public void deleteTourDate(Date date,Long id){
        tourRepo.removeDate(date,id);
    }

    public void sendEmailNotifications(Tour tour) {
        for (Date i : tour.getTourdate()) {
            LocalDate ldt=LocalDate.parse(i.toString());
            if (LocalDate.now().compareTo(ldt.minusDays(7))>=0) {
                List<BookedInfo> bi = bookedInfoService.getBookedInfoByTour(tour);
                for (BookedInfo b : bi) {
                    if (!b.getPaid()) {
                        Usr usr = b.getUserDetails().getUsr();
                        SimpleMailMessage mailMessage = new SimpleMailMessage();
                        mailMessage.setTo(usr.getEmail());
                        mailMessage.setSubject("The tour is close!");
                        mailMessage.setFrom("tourbooking@gmail.com");
                        mailMessage.setText("Dear " + usr.getUsername() +"the tour that you have booked is close! Please" +
                                " pay for the tour! Link to the website: http://localhost:8080/main");

                        sender.sendEmail(mailMessage);
                    }
                }
            }
        }
    }

}
