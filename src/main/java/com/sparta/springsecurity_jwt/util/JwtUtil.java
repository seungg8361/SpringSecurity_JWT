package com.sparta.springsecurity_jwt.util;

import com.sparta.springsecurity_jwt.common.exception.CustomException;
import com.sparta.springsecurity_jwt.common.exception.ResponseCode;
import com.sparta.springsecurity_jwt.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.expiredAt}")
    private Long expiredAt;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] decodeKey = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(decodeKey);
    }

    // 토큰 생성
    public String createToken(User user) {
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(user.getId()))
                        .claim("username", user.getUsername())
                        .claim("role", user.getRole())
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + expiredAt))
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();
    }

    // JWT 유효성 검사 및 파싱
    public String validateToken(String token) {
        if (!StringUtils.hasText(token) || !token.startsWith(BEARER_PREFIX)) {
            return null;
        }
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.substring(7));
            return token.substring(7);
        }  catch (SecurityException | MalformedJwtException | UnsupportedJwtException
                  | ExpiredJwtException | IllegalArgumentException e) {
            throw new CustomException(ResponseCode.INVALID_TOKEN);
        }
    }

    // 토큰에서 사용자 정보 추출
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
