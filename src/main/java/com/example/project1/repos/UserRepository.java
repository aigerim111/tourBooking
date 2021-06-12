package com.example.project1.repos;

import com.example.project1.user.Usr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



@Repository
public interface UserRepository extends JpaRepository<Usr, Long> {

    Usr findByUsername(String username);
    Usr findUsrByEmail(String email);
    Usr findUsrById(Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Usr usr set usr.username =:username where usr.id =:id")
    public void updateUsername(@Param("username") String username, @Param("id") Long id);



}
