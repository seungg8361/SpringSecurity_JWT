package com.sparta.springsecurity_jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.springsecurity_jwt.common.exception.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 접근 권한이 없을 때 실행되는 메서드
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(ResponseCode.ACCESS_DENIED.getStatus().value()); // 403 Forbidden
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorBody = new HashMap<>();
        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("code", ResponseCode.ACCESS_DENIED.name());
        errorDetail.put("message", ResponseCode.ACCESS_DENIED.getMessage());
        errorBody.put("error", errorDetail);

        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}
