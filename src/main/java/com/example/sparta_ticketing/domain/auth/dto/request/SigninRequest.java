package com.example.sparta_ticketing.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SigninRequest {

    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
