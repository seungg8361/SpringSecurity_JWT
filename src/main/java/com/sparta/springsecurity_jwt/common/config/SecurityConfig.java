package com.sparta.springsecurity_jwt.common.config;

import com.sparta.springsecurity_jwt.filter.CustomAccessDeniedHandler;
import com.sparta.springsecurity_jwt.filter.JwtAuthenticationFilter;
import com.sparta.springsecurity_jwt.service.security.UserDetailsServiceImpl;
import com.sparta.springsecurity_jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // 보안 헤더 설정 비활성화 (H2 콘솔 접근)
        http.headers(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorizeRequests -> {
            authorizeRequests.
                    requestMatchers("/signUp", "/login").permitAll()
                    .requestMatchers("/v3/api-docs/**", "/docs", "/swagger-ui/**").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .anyRequest().authenticated();
        });

        // 인가 실패 시(권한 부족) 예외 핸들러 설정
        http.exceptionHandling(exception ->
                exception.accessDeniedHandler(new CustomAccessDeniedHandler()));

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 세션을 사용하지 않도록 Stateless 설정 (JWT 기반 인증이므로 세션 불필요)
        http.sessionManagement(securityFilterChain ->
                securityFilterChain.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
