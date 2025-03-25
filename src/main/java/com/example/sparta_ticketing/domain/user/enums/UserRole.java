package com.example.sparta_ticketing.domain.user.enums;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UserRole {
    ROLE_USER(Authority.USER),
    ROLE_ADMIN(Authority.ADMIN),
    ROLE_DIRECTOR(Authority.DIRECTOR);

    private final String userRole;

    UserRole(String userRole) {
        this.userRole = userRole;
    }

    public static UserRole of(String userRole) {

        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(userRole))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 USER_ROLE 입니다"));
    }

    public static class Authority {
        public static String USER = "ROLE_USER";
        public static String ADMIN = "ROLE_ADMIN";
        public static String DIRECTOR = "ROLE_DIRECTOR";
    }
}