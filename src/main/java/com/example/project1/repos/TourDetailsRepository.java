package com.example.project1.repos;

import com.example.project1.tours.Tour;
import com.example.project1.tours.TourDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourDetailsRepository extends JpaRepository<TourDetails,Long> {
    TourDetails findTourDetailsByTour(Tour tour);
}
