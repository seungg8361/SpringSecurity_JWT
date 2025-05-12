package com.sparta.springsecurity_jwt.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ErrorResponse {
    private final ErrorDetail error;

    public ErrorResponse(ResponseCode code) {
        this.error = new ErrorDetail(code.name(), code.getMessage());
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ErrorDetail {
        private String code;
        private String message;
    }
}
