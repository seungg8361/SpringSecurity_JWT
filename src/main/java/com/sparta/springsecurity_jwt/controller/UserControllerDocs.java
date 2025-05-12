package com.sparta.springsecurity_jwt.controller;

import com.sparta.springsecurity_jwt.dto.request.LoginRequestDto;
import com.sparta.springsecurity_jwt.dto.request.SignUpRequestDto;
import com.sparta.springsecurity_jwt.dto.response.LoginResponseDto;
import com.sparta.springsecurity_jwt.dto.response.UserInfoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "유저 관련 API", description = "회원가입, 로그인, 권한 부여에 대한 API 명세서 입니다.")
public abstract class UserControllerDocs {

    @Operation(summary = "회원가입", description = "사용자 회원가입을 처리합니다.")
    abstract ResponseEntity<UserInfoResponseDto> signUp(@RequestBody SignUpRequestDto requestDto);

    @Operation(summary = "로그인", description = "사용자 로그인을 처리하고 토큰을 발급합니다.")
    abstract ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto);
}
