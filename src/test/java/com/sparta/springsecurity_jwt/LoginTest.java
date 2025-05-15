package com.sparta.springsecurity_jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.springsecurity_jwt.domain.User;
import com.sparta.springsecurity_jwt.domain.UserRole;
import com.sparta.springsecurity_jwt.dto.request.LoginRequestDto;
import com.sparta.springsecurity_jwt.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LoginTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String PASSWORD = "password";
    private static final String NICKNAME = "nickname";

    private void createNormalUser(String username) {
        userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User user = User.builder()
                            .username(username)
                            .password(bCryptPasswordEncoder.encode(PASSWORD))
                            .nickname(NICKNAME)
                            .role(UserRole.USER)
                            .build();
                    return userRepository.save(user);
                });
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {
        String username = "loginSuccessUser";
        createNormalUser(username);

        LoginRequestDto requestDto = new LoginRequestDto(username, PASSWORD);
        String json = objectMapper.writeValueAsString(requestDto);

        mvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    void loginFail_UserNotFound() throws Exception {
        createNormalUser("existingUser2");

        LoginRequestDto requestDto = new LoginRequestDto("nonExistentUser2", PASSWORD);
        String json = objectMapper.writeValueAsString(requestDto);

        mvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginFail_InvalidCredentials() throws Exception {
        String username = "loginFailInvalidCredentialsUser";
        createNormalUser(username);

        LoginRequestDto requestDto = new LoginRequestDto(username, "wrongPassword");
        String json = objectMapper.writeValueAsString(requestDto);

        mvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
