package com.example.sparta_ticketing.domain.auth.service;

import com.example.sparta_ticketing.common.exception.AuthException;
import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.common.exception.UserNotFoundException;
import com.example.sparta_ticketing.common.security.JwtUtil;
import com.example.sparta_ticketing.domain.auth.dto.request.SigninRequest;
import com.example.sparta_ticketing.domain.auth.dto.request.SignupRequest;
import com.example.sparta_ticketing.domain.auth.dto.response.SigninResponse;
import com.example.sparta_ticketing.domain.auth.dto.response.SignupResponse;
import com.example.sparta_ticketing.domain.user.entity.User;
import com.example.sparta_ticketing.domain.user.enums.UserRole;
import com.example.sparta_ticketing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private JwtUtil jwtUtil;

    @Test
    void 회원가입_성공_테스트() {
        // given
        String email = "test@example.com";
        String password = "password123";
        String nickname = "르탄이";
        String phoneNumber = "010-0000-0000";
        String birthday = "2000-01-01";
        String userRole = "ROLE_USER";
        UserRole userRole2 = UserRole.ROLE_USER;

        SignupRequest signupRequest = new SignupRequest(email, password, nickname, phoneNumber, birthday, userRole);

        User user = new User(email, password, nickname, phoneNumber, birthday, userRole2);

        String bearerToken = "someBearerToken";

        given(userRepository.existsByEmail(any(String.class))).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user); // Mock으로 저장된 사용자 반환
        given(jwtUtil.createToken(user.getId(), email, nickname, UserRole.ROLE_USER)).willReturn(bearerToken); // Mock으로 JWT 반환

        // when
        SignupResponse response = authService.signup(signupRequest);

        // then
        assertNotNull(response.getJwtToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 이메일이_중복돼서_회원가입_실패하는_테스트() {
        // given
        String email = "test@example.com";
        String password = "password123";
        String nickname = "르탄이";
        String phoneNumber = "010-0000-0000";
        String birthday = "2000-01-01";
        String userRole = "ROLE_USER";

        SignupRequest signupRequest = new SignupRequest(email, password, nickname, phoneNumber, birthday, userRole);

        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(true);

        // when& then
        assertThrows(InvalidRequestException.class, () -> authService.signup(signupRequest));
    }

    @Test
    void 관리자_권한은_회원가입_실패하는_테스트() {
        // given
        String email = "test@example.com";
        String password = "password123";
        String nickname = "르탄이";
        String phoneNumber = "010-0000-0000";
        String birthday = "2000-01-01";
        String userRole = "ROLE_ADMIN";

        SignupRequest signupRequest = new SignupRequest(email, password, nickname, phoneNumber, birthday, userRole);

        // when& then
        assertTrue(signupRequest.getUserRole().equals("ROLE_ADMIN"));
        assertThrows(AuthException.class, () -> authService.signup(signupRequest));
    }

    @Test
    void 로그인_성공_테스트() {
        // given
        String email = "test@example.com";
        String password = "password123";
        String nickname = "르탄이";
        String phoneNumber = "010-0000-0000";
        String birthday = "2000-01-01";
        UserRole userRole2 = UserRole.ROLE_USER;
        String encodedPassword = password;
        String bearerToken = "someBearerToken";
        User user = new User(email, password, nickname, phoneNumber, birthday, userRole2);
        SigninRequest signinRequest = new SigninRequest(email, password);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);
        given(jwtUtil.createToken(user.getId(), email, nickname, userRole2)).willReturn(bearerToken);

        // when
        SigninResponse response = authService.signin(signinRequest);

        // then
        assertNotNull(response.getJwtToken());
        assertEquals("someBearerToken", bearerToken, response.getJwtToken());
    }


    @Test
    void 존재하지_않는_이메일로_로그인_하면_실패하는_테스트() {
        // given
        String email = "test@example.com";
        String password = "password123";
        String nickname = "르탄이";
        String phoneNumber = "010-0000-0000";
        String birthday = "2000-01-01";
        UserRole userRole2 = UserRole.ROLE_USER;
        User user = new User(email, password, nickname, phoneNumber, birthday, userRole2);
        SigninRequest signinRequest = new SigninRequest(email, password);

        given(userRepository.findByEmail(any(String.class))).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> authService.signin(signinRequest));
    }

    @Test
    void 비밀번호_틀리면_로그인_실패하는_테스트() {
        // given
        String email = "test@example.com";
        String password = "password123";
        String nickname = "르탄이";
        String phoneNumber = "010-0000-0000";
        String birthday = "2000-01-01";
        UserRole userRole2 = UserRole.ROLE_USER;
        String encodedPassword = password;
        User user = new User(email, password, nickname, phoneNumber, birthday, userRole2);

        SigninRequest signinRequest = new SigninRequest(email, password);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        given(passwordEncoder.matches(password, encodedPassword)).willReturn(false);

        // when & then
        assertThrows(AuthException.class, () -> authService.signin(signinRequest));
    }
}


