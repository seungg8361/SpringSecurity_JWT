package com.sparta.springsecurity_jwt.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequestDto(

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Schema(example = "JIN HO")
        String username,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Schema(example = "12341234")
        String password,

        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Schema(example = "Mentos")
        String nickname
) {}
