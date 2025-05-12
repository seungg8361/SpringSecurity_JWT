package com.sparta.springsecurity_jwt.util;

import com.sparta.springsecurity_jwt.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private static final String AUTHORIZATION_HEADER = "Authorization";

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
                    .claim("role", user.getRole())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiredAt))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
    }

    // 쿠키에 토큰 담기
    public void addJwtToCookie(String token, HttpServletResponse response){
        try{
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+","%20");

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token);
            cookie.setPath("/");

            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    // 토큰 검증
    public String validateToken(String token){
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)){
            try{
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
                return token.substring(7);
            }catch (ExpiredJwtException e) {
                log.warn("JWT 만료됨: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                log.warn("JWT 형식 오류: {}", e.getMessage());
            } catch (Exception e) {
                log.error("JWT 오류: {}", e.getMessage());
            }
        }
        return null;
    }

    // 토큰에서 사용자 정보 추출
    public Claims getClaimsFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
