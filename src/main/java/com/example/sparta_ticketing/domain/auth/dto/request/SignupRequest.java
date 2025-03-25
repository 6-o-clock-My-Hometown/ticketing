package com.example.sparta_ticketing.domain.auth.dto.request;

import com.example.sparta_ticketing.domain.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    @Email @NotBlank
    private String email;

    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    private String password;

    @NotBlank
    private String nickname;

    @Pattern(regexp = "^01[0-1|6-9]-\\d{4}-\\d{4}$", message = "'01X-XXXX-XXXX'")
    private String phoneNumber;

    @NotBlank
    private String birthday;

    @NotBlank
    private String userRole;
}
