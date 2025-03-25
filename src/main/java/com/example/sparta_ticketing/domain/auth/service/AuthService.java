package com.example.sparta_ticketing.domain.auth.service;

import com.example.sparta_ticketing.config.JwtUtil;
import com.example.sparta_ticketing.domain.auth.dto.request.SignupRequest;
import com.example.sparta_ticketing.domain.auth.dto.response.SignupResponse;
import com.example.sparta_ticketing.domain.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.domain.user.entity.User;
import com.example.sparta_ticketing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

//        new User(request.getEmail(), encodedPassword, request.getNickname(), request.getUserRole())

//        jwtUtil.createToken()

        return new SignupResponse();

    }
}
