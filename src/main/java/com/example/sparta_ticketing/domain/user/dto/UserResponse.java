package com.example.sparta_ticketing.domain.user.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String birthday;
    private final String phoneNumber;

    public UserResponse(Long id, String email, String nickname, String birthday, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }

}
