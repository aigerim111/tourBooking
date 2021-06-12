package com.example.project1.config.emailToken;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationToken extends JpaRepository<EmailToken,Long> {
   EmailToken findByToken(String Token);
}
