package com.sparta.springsecurity_jwt.controller;

import com.sparta.springsecurity_jwt.common.exception.ErrorResponse;
import com.sparta.springsecurity_jwt.dto.request.LoginRequestDto;
import com.sparta.springsecurity_jwt.dto.request.SignUpRequestDto;
import com.sparta.springsecurity_jwt.dto.response.LoginResponseDto;
import com.sparta.springsecurity_jwt.dto.response.UserInfoResponseDto;
import com.sparta.springsecurity_jwt.service.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "유저 관련 API", description = "회원가입, 로그인, 권한 부여에 대한 API 명세서 입니다.")
public interface UserControllerDocs {

    @Operation(summary = "회원가입", description = "사용자 회원가입을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = UserInfoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "이미 가입된 사용자입니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": {\"code\": \"USER_ALREADY_EXISTS\", \"message\": \"이미 가입된 사용자입니다.\"}}")
                    )),
            @ApiResponse(responseCode = "401", description = "(관리자 회원가입용) 관리자 코드가 올바르지 않습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": {\"code\": \"INVALID_ADMIN_CODE\", \"message\": \"관리자 코드가 올바르지 않습니다.\"}}")
                    ))
    })
    ResponseEntity<UserInfoResponseDto> signUp(@RequestBody SignUpRequestDto requestDto);

    @Operation(summary = "로그인", description = "사용자 로그인을 처리하고 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 올바르지 않습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": {\"code\": \"INVALID_CREDENTIALS\", \"message\": \"아이디 또는 비밀번호가 올바르지 않습니다.\"}}")
                    )),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자입니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": {\"code\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자입니다.\"}}")
                    ))
    })
    ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto);

    @Operation(summary = "관리자 권한 부여", description = "사용자에게 관리자 권한을 부여합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "권한 부여에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = UserInfoResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 인증 토큰입니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": {\"code\": \"INVALID_TOKEN\", \"message\": \"유효하지 않은 인증 토큰입니다.\"}}")
                    )),
            @ApiResponse(responseCode = "403", description = "관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": {\"code\": \"ACCESS_DENIED\", \"message\": \"관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다.\"}}")
                    )),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자입니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": {\"code\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자입니다.\"}}")
                    ))
    })
    ResponseEntity<UserInfoResponseDto> changeRole(@PathVariable("userId") Long userId,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails);
}
