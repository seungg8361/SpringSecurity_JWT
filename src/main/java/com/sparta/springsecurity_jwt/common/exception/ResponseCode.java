package com.sparta.springsecurity_jwt.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 400, "이미 가입된 사용자입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "존재하지 않는 사용자입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, 401, "아이디 또는 비밀번호가 올바르지 않습니다.");

    private final HttpStatus status;
    private final Integer code;
    private final String message;
}
