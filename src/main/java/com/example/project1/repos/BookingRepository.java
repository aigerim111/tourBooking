package com.example.project1.repos;

import com.example.project1.tours.Tour;
import com.example.project1.user.BookedInfo;
import com.example.project1.user.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookedInfo,Long> {
    @Transactional
    @Query(value = "select det_id from booking where bookid=:bookid",nativeQuery = true)
    UserDetails returnUserDetails(@Param("bookid") Long bookid);

    @Transactional
    @Query(value = "select booking.selTourDate from booking join booking_tours on booking.bookid=booking_tours.booked_info_bookid where tours_tourid=:tour",nativeQuery = true)
    List<Date> dateList (@Param("tour")  Long tour);

    @Transactional
    @Query(value = "SELECT booking_tours.tours_tourid FROM booking_tours INNER JOIN (SELECT COUNT(*) AS the_count, tours_tourid FROM booking_tours GROUP BY tours_tourid) t ON booking_tours.tours_tourid = t.tours_tourid group by t.the_count, booking_tours.tours_tourid ORDER BY t.the_count desc fetch first 6 rows only",nativeQuery = true)
    List<Long> listOfMostBookedTours();

    List<BookedInfo> findBookedInfoByUserDetails(UserDetails userDetails);

    BookedInfo findBookedInfoByBookid(Long id);

//    @Transactional
//    @Query(value = "select bookied_info_bookid from booking_tours where tour ",nativeQuery = true)

    List<BookedInfo> findBookedInfoByTours(Tour tour);

}
