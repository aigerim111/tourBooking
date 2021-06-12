package com.example.project1.service;


import com.example.project1.repos.BookingRepository;
import com.example.project1.tours.Tour;
import com.example.project1.user.BookedInfo;
import com.example.project1.user.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookedInfoService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    TourService tourService;

    public void saveBookingInfo(BookedInfo bookedInfo){
        bookingRepository.save(bookedInfo);
    }

//    public String getSelDate(Long tour){
//        String dates="";
//        for(Date i:bookingRepository.dates(tour)){
//            dates+=String.valueOf(i)+" ";
//        }
//        return dates;
//    }

    public List<Date> getSelDateList(Long tourid){
        return bookingRepository.dateList(tourid);
    }

    public List<BookedInfo> getBookingInfo(UserDetails userDetails) {
        return bookingRepository.findBookedInfoByUserDetails(userDetails);
    }


    public List<Tour> tourRelevance(){
        ArrayList<Tour> tours=new ArrayList<>();
        for(Long i:bookingRepository.listOfMostBookedTours()){
            tours.add(tourService.findingTourById(i));
        }
        return tours;
    }

    public void deleteBookingInfo(BookedInfo bi){
        bookingRepository.delete(bi);
    }

    public BookedInfo getBookingInfoById(Long id){
        return bookingRepository.findBookedInfoByBookid(id);
    }

    public List<BookedInfo> getBookedInfoByTour(Tour tour){
        return bookingRepository.findBookedInfoByTours(tour);
    }

    public UserDetails userDetailsByBookedInfo(Long id){
        return bookingRepository.returnUserDetails(id);
    }
}
