package com.sparta.springsecurity_jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.springsecurity_jwt.domain.User;
import com.sparta.springsecurity_jwt.domain.UserRole;
import com.sparta.springsecurity_jwt.dto.request.LoginRequestDto;
import com.sparta.springsecurity_jwt.dto.request.SignUpRequestDto;
import com.sparta.springsecurity_jwt.repository.UserRepository;
import com.sparta.springsecurity_jwt.service.security.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class SpringSecurityJwtApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "password";
    private static final String NICKNAME = "nickname";
    private static final String ADMIN_CODE = "iamadmin";
    private static final String WRONG_ADMIN_CODE = "idontnocode";

    @BeforeEach
    void setUp() {
        // 테스트 전 SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    private User createNormalUser(String username) {
        User user = User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .nickname(NICKNAME)
                .role(UserRole.USER)
                .build();
        return userRepository.save(user);
    }

    private User createAdminUser(String username) {
        User admin = User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .nickname(NICKNAME)
                .role(UserRole.ADMIN)
                .build();
        return userRepository.save(admin);
    }


    // 사용자로 인증 컨텍스트 설정
    private void setAuthenticationContext(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String role = user.getRole() == UserRole.ADMIN ? "ROLE_ADMIN" : "ROLE_USER";
        TestingAuthenticationToken auth = new TestingAuthenticationToken(userDetails, null, role);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

// ============================================ 회원가입 테스트 ============================================

    @Test
    @DisplayName("회원가입 성공 테스트 - 일반 유저")
    public void signUpUserSuccessTest() throws Exception {
        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .nickname(NICKNAME)
                .build();

        String json = objectMapper.writeValueAsString(requestDto);
        mvc.perform(
                MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpectAll(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("회원가입 성공 테스트 - 관리자")
    public void signUpAdminSuccessTest() throws Exception {
        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .nickname(NICKNAME)
                .adminCode(ADMIN_CODE) // 관리자 코드 일치
                .build();

        String json = objectMapper.writeValueAsString(requestDto);
        mvc.perform(
                MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpectAll(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 존재하는 유저")
    public void signUpFailTest_USER_ALREADY_EXISTS() throws Exception {
        createNormalUser(USERNAME);

        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .username(USERNAME) // 존재하는 유저
                .password(PASSWORD)
                .nickname(NICKNAME)
                .build();

        String json = objectMapper.writeValueAsString(requestDto);
        mvc.perform(
                MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 관리자 코드 불일치")
    public void signUpAdminFailTest_INVALID_CODE() throws Exception {
        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .nickname(NICKNAME)
                .adminCode(WRONG_ADMIN_CODE) // 코드 불일치
                .build();

        String json = objectMapper.writeValueAsString(requestDto);
        mvc.perform(
                MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpectAll(MockMvcResultMatchers.status().isUnauthorized());
    }

// ============================================ 로그인 테스트 ============================================

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccessTest() throws Exception {
        createNormalUser(USERNAME);

        LoginRequestDto requestDto = new LoginRequestDto(USERNAME, PASSWORD);

        String json = objectMapper.writeValueAsString(requestDto);
        mvc.perform(
                MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpectAll(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 존재하지 않는 사용자")
    public void loginFail_USER_NOT_FOUND() throws Exception {
        createNormalUser("existingUser");

        LoginRequestDto requestDto = new LoginRequestDto("nonExistentUser", PASSWORD);

        String json = objectMapper.writeValueAsString(requestDto);
        mvc.perform(
                MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    public void loginFail_INVALID_CREDENTIALS() throws Exception {
        createNormalUser(USERNAME);

        LoginRequestDto requestDto = new LoginRequestDto(USERNAME, "wrongPassword");

        String json = objectMapper.writeValueAsString(requestDto);
        mvc.perform(
                MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

// ========================================== 관리자 권한 부여 테스트 ============================================

    @Test
    @DisplayName("관리자 권한 부여 성공 테스트")
    public void access_RoleSuccessTest() throws Exception {
        User user = createNormalUser("user");

        User admin = createAdminUser("adminUser");
        setAuthenticationContext(admin);

        mvc.perform(
                MockMvcRequestBuilders.patch("/admin/users/{userId}/roles", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(MockMvcResultMatchers.status().isOk());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(UserRole.ADMIN, updatedUser.getRole());
    }

    @Test
    @DisplayName("관리자 권한 부여 실패 테스트 - 존재하지 않는 유저")
    public void access_RoleFailTest_USER_NOT_FOUND() throws Exception {
        User admin = createAdminUser("adminUser");
        setAuthenticationContext(admin);

        Long nonExistentUserId = 9999L;
        mvc.perform(
                MockMvcRequestBuilders.patch("/admin/users/{userId}/roles", nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("관리자 권한 부여 실패 테스트 - 접근 권한이 없음")
    public void access_RoleFailTest_ACCESS_DENIED() throws Exception {
        User user = createNormalUser("user");

        User normalUser = createNormalUser("normalUser");
        setAuthenticationContext(normalUser);

        mvc.perform(
                MockMvcRequestBuilders.patch("/admin/users/{userId}/roles", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(MockMvcResultMatchers.status().isForbidden());
    }
}