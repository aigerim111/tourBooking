package com.example.project1.tours;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
public class Tour {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(name="tourid")
    private Long tourid;

    @Column(nullable = false,name = "tourname")
    private String tourname;

    @Column(nullable = false,name = "address")
    private String address;

    @Type(type="list-array")
    @Column(nullable = false,name = "tourdate",
    columnDefinition = "date[]"
    )
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private List<Date> tourdate;

    @Column(nullable = false,name = "price")
    private Integer price;

    @Column(nullable = false)
    private boolean isActive=false;

    @Column(name = "image")
    private String image;

    @Column (name="tourduration")
    private String tourduration;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="tourDetId")
    private TourDetails tourDetails;

    public void setTour(Tour tour) {
        this.tourid=tour.tourid;
        this.tourname =tour.getTourname();
        this.address = tour.getAddress();
        this.price = tour.getPrice();
        this.image=tour.getImage();
        this.tourduration=tour.getTourduration();
    }

    public Tour(){

    }

    public String getTourname() {
        return tourname;
    }

    public String getAddress() {
        return address;
    }

    public List<Date> getTourdate() {
        return tourdate;
    }

    public Date getTheNearestDate(){
        Date theNearestDate= Collections.min(this.tourdate);
        return theNearestDate;
    }

    public Integer getPrice() {
        return price;
    }

    public Long getTourid() {
        return tourid;
    }

    public void setTourname(String tourname) {
        this.tourname = tourname;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTourdate(String tourdate) throws ParseException {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        Date d = sdformat.parse(tourdate);
        if(this.tourdate==null){
            List<Date> dates=new ArrayList<>();
            dates.add(d);
            this.tourdate=dates;
        }else {
            this.tourdate.add(d);
        }
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTourduration() {
        return tourduration;
    }

    public void setTourduration(String tourduration) {
        this.tourduration = tourduration;
    }

    public void setActive() {
        Date now=new Date();
        int count=0;
        for(int i=0;i<this.tourdate.size();i++) {
            if (this.tourdate.get(i).compareTo(now) < 0 || this.tourdate.get(i).compareTo(now) == 0) {
                this.tourdate.remove(i);
                i=0;
            }
            if (this.tourdate.get(i).compareTo(now) > 0) {
                count++;
            }
        }
        if(count==0) this.isActive=false;
        if(count>0) this.isActive=true;
    }

    public void setnonActive(){
        this.isActive=false;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean getTourDetails() {
        if(this.tourDetails==null) return false;
        else return true;
    }

    public void setTourDetails(TourDetails tourDetails) {
        this.tourDetails = tourDetails;
    }
}
