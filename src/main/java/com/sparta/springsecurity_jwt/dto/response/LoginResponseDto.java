package com.sparta.springsecurity_jwt.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponseDto(

        @Schema(example = "{token}",
                description = "로그인에 성공 시 발급되는 JWT token")
        String token
) {
    public static LoginResponseDto of(String token) {
        return new LoginResponseDto(token);
    }
}
