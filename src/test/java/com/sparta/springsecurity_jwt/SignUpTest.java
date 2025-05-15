package com.sparta.springsecurity_jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.springsecurity_jwt.domain.User;
import com.sparta.springsecurity_jwt.domain.UserRole;
import com.sparta.springsecurity_jwt.dto.request.SignUpRequestDto;
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
public class SignUpTest {

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
    private static final String ADMIN_CODE = "iamadmin";
    private static final String WRONG_ADMIN_CODE = "idontnocode";

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
    @DisplayName("회원가입 성공 - 일반 유저")
    void signUpUserSuccess() throws Exception {
        String username = "signUpUserSuccess";

        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .username(username)
                .password(PASSWORD)
                .nickname(NICKNAME)
                .build();

        String json = objectMapper.writeValueAsString(requestDto);
        mvc.perform(MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("회원가입 성공 - 관리자")
    void signUpAdminSuccess() throws Exception {
        String username = "signUpAdminSuccess";

        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .username(username)
                .password(PASSWORD)
                .nickname(NICKNAME)
                .adminCode(ADMIN_CODE)
                .build();

        String json = objectMapper.writeValueAsString(requestDto);
        mvc.perform(MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 유저")
    void signUpFail_UserAlreadyExists() throws Exception {
        String username = "userAlreadyExists";
        createNormalUser(username);

        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .username(username)
                .password(PASSWORD)
                .nickname(NICKNAME)
                .build();

        String json = objectMapper.writeValueAsString(requestDto);
        mvc.perform(MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 실패 - 관리자 코드 불일치")
    void signUpFail_InvalidAdminCode() throws Exception {
        String username = "signUpFailInvalidAdminCode";

        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .username(username)
                .password(PASSWORD)
                .nickname(NICKNAME)
                .adminCode(WRONG_ADMIN_CODE)
                .build();

        String json = objectMapper.writeValueAsString(requestDto);
        mvc.perform(MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
