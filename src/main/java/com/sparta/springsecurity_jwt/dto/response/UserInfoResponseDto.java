package com.sparta.springsecurity_jwt.dto.response;

import com.sparta.springsecurity_jwt.domain.User;
import com.sparta.springsecurity_jwt.domain.UserRole;

public record UserInfoResponseDto(
        String username,
        String nickname,
        UserRole role
) {

    public static UserInfoResponseDto of(User user) {
        return new UserInfoResponseDto(user.getUsername(), user.getNickname(), user.getRole());
    }
}
