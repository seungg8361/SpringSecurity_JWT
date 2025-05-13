package com.sparta.springsecurity_jwt.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequestDto {

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Schema(
                example = "JIN HO",
                description = "회원가입 시 입력하는 사용자 이름")
        private String username;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Schema(example = "12341234",
                description = "회원가입 시 입력하는 비밀번호")
        private String password;

        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Schema(example = "Mentos",
                description = "회원가입 시 입력하는 닉네임")
        private String nickname;

        @Schema(
                example = "security_admin_code",
                description = "관리자 회원가입 시 입력하는 관리자 코드 (일반 사용자는 생략 가능)",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        private String adminCode;
}
