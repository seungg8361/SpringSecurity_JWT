package com.sparta.springsecurity_jwt.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ErrorResponse {

    @Schema(description = "에러 정보", implementation = ErrorDetail.class)
    private final ErrorDetail error;

    public ErrorResponse(ResponseCode code) {
        this.error = new ErrorDetail(code.name(), code.getMessage());
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ErrorDetail {
        @Schema(description = "에러 코드")
        private String code;
        @Schema(description = "에러 메시지")
        private String message;
    }
}
