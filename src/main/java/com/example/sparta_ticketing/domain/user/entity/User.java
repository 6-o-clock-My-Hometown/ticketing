package com.example.sparta_ticketing.domain.user.entity;


import com.example.sparta_ticketing.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;

    private String nickname;
    private LocalDate birthday;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public User(String email, String password, String nickname, UserRole userRole, LocalDate birthday, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.userRole = userRole;
    }

    private User(Long id, String email, String nickname, UserRole userRole, LocalDate birthday, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.userRole = userRole;
    }

    private void userUpdate(String nickname,  String phoneNumber ){
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }

    public void updateUser(String nickname, String phoneNumber) {
    }
}
