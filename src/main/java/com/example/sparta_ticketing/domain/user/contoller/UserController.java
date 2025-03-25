package com.example.sparta_ticketing.domain.user.contoller;

import com.example.sparta_ticketing.domain.auth.entity.AuthUser;
import com.example.sparta_ticketing.domain.user.dto.UserResponse;
import com.example.sparta_ticketing.domain.user.dto.UserUpdateRequest;
import com.example.sparta_ticketing.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profiles")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(userService.getUser(authUser));
    }

    @PutMapping("/profiles")
    public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(userService.updateUser(authUser, userUpdateRequest));
    }
}