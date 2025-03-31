package com.example.sparta_ticketing.domain.auth.service;

import com.example.sparta_ticketing.common.security.JwtUtil;
import com.example.sparta_ticketing.common.exception.AuthException;
import com.example.sparta_ticketing.common.exception.UserNotFoundException;
import com.example.sparta_ticketing.domain.auth.dto.request.SigninRequest;
import com.example.sparta_ticketing.domain.auth.dto.request.SignupRequest;
import com.example.sparta_ticketing.domain.auth.dto.response.SigninResponse;
import com.example.sparta_ticketing.domain.auth.dto.response.SignupResponse;
import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.domain.user.entity.User;
import com.example.sparta_ticketing.domain.user.enums.UserRole;
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

        if (request.getUserRole().equals("ROLE_ADMIN")) {
            throw new AuthException("관리자로 가입이 불가능합니다");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getPhoneNumber(),
                request.getBirthday(),
                UserRole.of(request.getUserRole()));

        User savedUser = userRepository.save(user);

        // JWT 생성
        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getUserRole());

        return new SignupResponse(bearerToken);
    }

    public SigninResponse signin(SigninRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("해당 이메일을 가진 사용자가 없습니다"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))  {
            throw new AuthException("비밀번호가 일치하지 않습니다");
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getNickname(), user.getUserRole());

        return new SigninResponse(bearerToken);
    }
}
