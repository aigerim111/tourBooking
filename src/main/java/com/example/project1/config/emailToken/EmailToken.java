package com.example.project1.config.emailToken;



import com.example.project1.user.Usr;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class EmailToken {
    @Id
    @GeneratedValue(strategy =GenerationType.SEQUENCE)
    private Long tokenId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(
            nullable = false,
            name = "id"
    )
    private Usr usr;

    public EmailToken(Usr usr) {
        this.token = UUID.randomUUID().toString();
        this.createdDate=LocalDateTime.now();
        this.expiryDate = createdDate.plusHours(24);
        this.usr = usr;
    }

    public EmailToken() {

    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setUsr(Usr usr) {
        this.usr = usr;
    }

    public Long getId() {
        return usr.getId();
    }

    public Long getTokenId(){
        return tokenId;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public Usr getUsr() {
        return usr;
    }
}
