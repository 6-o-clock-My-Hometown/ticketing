package com.example.sparta_ticketing.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @NotBlank
    private String nickname;
    @NotBlank
    private String password;
    @NotBlank
    @Pattern(regexp = "^01[0-1|6-9]-\\d{4}-\\d{4}$", message = "'01X-XXXX-XXXX' ")
    private String phoneNumber;
}
