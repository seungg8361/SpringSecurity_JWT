package com.sparta.springsecurity_jwt.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Schema(
                example = "JIN HO",
                description = "로그인 시 입력하는 사용자 이름")
        String username,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Schema(example = "12341234",
                description = "로그인 시 입력하는 비밀번호")
        String password
) {
}
