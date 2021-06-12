package com.example.project1.repos;

import com.example.project1.tours.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Repository
public interface TourRepository extends JpaRepository<Tour,Long> {
    Page<Tour> findAll(Pageable pageable);
    Tour findTourByTourid(Long Id);
    List<Tour> findAll();

    @Transactional
    @Modifying
    @Query(value = "update tour set tourdate=array_remove(tourdate, :deltourdate) where tourid=:tourid",nativeQuery = true)
    void removeDate(@Param("deltourdate") Date date, @Param("tourid") Long tourid);




}
