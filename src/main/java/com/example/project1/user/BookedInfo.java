package com.example.project1.user;

import com.example.project1.tours.Tour;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Table(name="booking")
@Entity
public class BookedInfo {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long bookid;

    @Column(name = "seltourdate")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date selTourDate;

    @Column(name = "numOfPeople")
    private Integer person;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="detId")
    private UserDetails userDetails;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "bookid")
    private List<Tour> tours;

    @Column
    Integer price;

    @Column
    Boolean paid;

    public void setBookingInfo(Date selTourDate, UserDetails userDetails, Tour tours,Integer person) {
        this.selTourDate = selTourDate;
        this.userDetails = userDetails;
        if(this.tours==null){
            setTours(tours);
        }else {this.tours.add(tours);}
        this.price=tours.getPrice()*person;
        this.person=person;
        this.paid=false;
    }

    public String getSelTourDate(){
        return String.valueOf(this.selTourDate);
    }
    public String getSelTourDate(List<Date> dateList) {
        String dates="";
        for(Date i:dateList){
            dates+= String.valueOf(i)+" ";
        }
        return dates;
    }

    public void setSelTourDate(Date selTourDate) {
        this.selTourDate=selTourDate;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public List<Tour> getTours() {
        return tours;
    }

    public void setTours(Tour tour) {
        List<Tour> tours1=new ArrayList<>();
        tours1.add(tour);
        this.tours=tours1;
    }

    public Integer getPerson() {
        return person;
    }

    public void setPerson(Integer person) {
        this.person = person;
    }

    public Long getBookid() {
        return bookid;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}
