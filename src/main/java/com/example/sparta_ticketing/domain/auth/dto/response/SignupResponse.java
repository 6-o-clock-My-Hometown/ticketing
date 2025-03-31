package com.example.sparta_ticketing.domain.auth.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class SignupResponse {

    private String jwtToken;

    public SignupResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

}
