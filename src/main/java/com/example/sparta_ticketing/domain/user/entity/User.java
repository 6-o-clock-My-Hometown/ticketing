package com.example.sparta_ticketing.domain.user.entity;

import com.example.sparta_ticketing.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String nickname;

    private String phoneNumber;

    private String birthday;

    private UserRole userRole;

    public User(String email, String password, String nickname, String phoneNumber, String birthday, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.userRole = userRole;
    }
}
