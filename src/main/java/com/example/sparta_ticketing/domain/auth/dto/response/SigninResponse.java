package com.example.sparta_ticketing.domain.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SigninResponse {

    private final String jwtToken;
}
