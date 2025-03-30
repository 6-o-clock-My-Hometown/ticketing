package com.example.sparta_ticketing.domain.user.service;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.domain.auth.entity.AuthUser;
import com.example.sparta_ticketing.domain.user.dto.UserResponse;
import com.example.sparta_ticketing.domain.user.dto.UserUpdateRequest;
import com.example.sparta_ticketing.domain.user.entity.User;
import com.example.sparta_ticketing.domain.user.enums.UserRole;
import com.example.sparta_ticketing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void 사용자_정보_조회() {
        // given
        User user = new User("user@example.com",
                passwordEncoder.encode("password"),
                "nickname",
                "010-1234-5678",
                "2020-01-01",
                UserRole.ROLE_USER);
        userRepository.save(user);

        AuthUser authUser = new AuthUser(user.getId(), user.getEmail(), user.getUserRole());

        // when
        UserResponse response = userService.getUser(authUser);

        // then
        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void 존재하지_않는_사용자_조회() {
        // given
        AuthUser authUser = new AuthUser(999L, "notfound@example.com", UserRole.ROLE_USER);

        // when & then
        assertThrows(InvalidRequestException.class, () -> userService.getUser(authUser));
    }

    @Test
    void 사용자_정보_정상수정() {
        // given
        String oldPassword = "password123";
        User user = new User("user1@example.com", passwordEncoder.encode(oldPassword), "nickname",
                "010-1234-5678", "2020-01-01", UserRole.ROLE_USER);
        userRepository.save(user);

        AuthUser authUser = new AuthUser(user.getId(), user.getEmail(), user.getUserRole());

        UserUpdateRequest request = new UserUpdateRequest("newNickname", oldPassword, "010-1111-2222");

        // when
        UserResponse response = userService.updateUser(authUser, request);

        // then
        assertNotNull(response);
        assertEquals("newNickname", response.getNickname());
        assertEquals("010-1111-2222", response.getPhoneNumber());
    }

    @Test
    void 잘못된_비밀번호로_사용자_정보_수정_시_예외발생() {
        // given
        User user = new User("userUpdate@example.com", passwordEncoder.encode("correctPassword"), "nickname",
                "010-1234-5678", "2020-01-01", UserRole.ROLE_USER);
        userRepository.save(user);

        AuthUser authUser = new AuthUser(user.getId(), user.getEmail(), user.getUserRole());

        UserUpdateRequest request = new UserUpdateRequest("newNickname", "010-1111-2222", "wrongPassword");

        // when & then
        assertThrows(InvalidRequestException.class, () -> userService.updateUser(authUser, request));
    }

    @Test
    void 사용자_조회_Optional() {
        // given
        User user = new User("optional@example.com", passwordEncoder.encode("correctPassword"), "nickname",
                "010-1234-5678", "2020-01-01", UserRole.ROLE_USER);

        userRepository.save(user);

        Optional<User> findUser = userService.findById(user.getId());

        assertEquals(user.getId(), findUser.get().getId());
        assertEquals(user.getEmail(), findUser.get().getEmail());
    }

}
