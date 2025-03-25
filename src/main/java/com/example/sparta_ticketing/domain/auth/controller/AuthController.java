package com.example.sparta_ticketing.domain.auth.controller;

import com.example.sparta_ticketing.domain.auth.dto.request.SignupRequest;
import com.example.sparta_ticketing.domain.auth.dto.response.SignupResponse;
import com.example.sparta_ticketing.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request))
    }
}
