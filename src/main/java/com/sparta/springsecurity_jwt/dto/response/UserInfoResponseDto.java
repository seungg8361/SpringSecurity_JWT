package com.sparta.springsecurity_jwt.dto.response;

import com.sparta.springsecurity_jwt.domain.User;
import com.sparta.springsecurity_jwt.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserInfoResponseDto(

        @Schema(
                example = "JIN HO",
                description = "회원가입 성공시 반환되는 사용자 이름")
        String username,
        @Schema(
                example = "Mentos",
                description = "회원가입 성공시 반환되는 닉네임")
        String nickname,
        @Schema(
                example = "USER or ADMIN",
                description = "회원가입 성공시 반환되는 사용자 권한")
        UserRole role
) {

    public static UserInfoResponseDto of(User user) {
        return new UserInfoResponseDto(user.getUsername(), user.getNickname(), user.getRole());
    }
}
