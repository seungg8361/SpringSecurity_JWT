package com.sparta.springsecurity_jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.springsecurity_jwt.common.exception.CustomException;
import com.sparta.springsecurity_jwt.common.exception.ResponseCode;
import com.sparta.springsecurity_jwt.service.security.UserDetailsImpl;
import com.sparta.springsecurity_jwt.service.security.UserDetailsServiceImpl;
import com.sparta.springsecurity_jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환용

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            String token = jwtUtil.validateToken(authHeader);
            if (token != null) {
                Claims claims = jwtUtil.getClaimsFromToken(token);
                String username = claims.get("username", String.class);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            sendErrorResponse(response, e.getResponseCode());
        } catch (Exception e) {
            sendErrorResponse(response, ResponseCode.INVALID_TOKEN);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, ResponseCode responseCode) throws IOException {
        response.setStatus(responseCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorBody = new HashMap<>();
        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("code", responseCode.name());
        errorDetail.put("message", responseCode.getMessage());
        errorBody.put("error", errorDetail);

        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}