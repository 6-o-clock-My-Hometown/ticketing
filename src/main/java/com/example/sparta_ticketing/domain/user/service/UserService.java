package com.example.sparta_ticketing.domain.user.service;

import com.example.sparta_ticketing.domain.auth.entity.AuthUser;
import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.domain.user.dto.UserResponse;
import com.example.sparta_ticketing.domain.user.dto.UserUpdateRequest;
import com.example.sparta_ticketing.domain.user.entity.User;
import com.example.sparta_ticketing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getUser(AuthUser authUser) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname(), user.getBirthday(), user.getPhoneNumber());
    }

    @Transactional
    public UserResponse updateUser(AuthUser authUser, UserUpdateRequest userUpdateRequest){

        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (!passwordEncoder.matches(userUpdateRequest.getPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.updateUser(userUpdateRequest.getNickname(), userUpdateRequest.getPhoneNumber());

        return new UserResponse(user.getId(), user.getEmail(), user.getNickname(), user.getBirthday(),  user.getPhoneNumber());

    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

}
