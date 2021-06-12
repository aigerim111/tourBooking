package com.example.project1.user;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
public class UserDetails {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long detId;

    @OneToOne
    @JoinColumn(
            nullable = false,
            name = "id"
    )
    private Usr usr;

   @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String secondName;

    @Column(nullable = false)
    @Size(min=5,max=90)
    private Integer age;

    @Column(nullable = false)
    private String city;

    @Column()
    private String phone;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "detId")
    private List<BookedInfo> bookedInfo;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Usr getUsr() {
        return usr;
    }

    public void setUsr(Usr usr) {
        this.usr = usr;
    }

    public UserDetails() {
    }

    public Long getDetId() {
        return detId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
