package com.example.sparta_ticketing.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SigninRequest {

    @NotBlank
    private String email;
    @NotBlank
    private String password;

}
