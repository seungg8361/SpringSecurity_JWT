package com.sparta.springsecurity_jwt.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ResponseCode code = ex.getResponseCode();
        return new ResponseEntity<>(
                new ErrorResponse(code),
                code.getStatus()
        );
    }
}
