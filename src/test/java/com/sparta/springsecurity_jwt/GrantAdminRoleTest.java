package com.sparta.springsecurity_jwt;

import com.sparta.springsecurity_jwt.domain.User;
import com.sparta.springsecurity_jwt.domain.UserRole;
import com.sparta.springsecurity_jwt.repository.UserRepository;
import com.sparta.springsecurity_jwt.service.security.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GrantAdminRoleTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String PASSWORD = "password";
    private static final String NICKNAME = "nickname";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private User createNormalUser(String username) {
        return userRepository.findByUsername(username)
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

    private User createAdminUser(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User admin = User.builder()
                            .username(username)
                            .password(bCryptPasswordEncoder.encode(PASSWORD))
                            .nickname(NICKNAME)
                            .role(UserRole.ADMIN)
                            .build();
                    return userRepository.save(admin);
                });
    }

    private void setAuthenticationContext(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String role = user.getRole() == UserRole.ADMIN ? "ROLE_ADMIN" : "ROLE_USER";
        TestingAuthenticationToken auth = new TestingAuthenticationToken(userDetails, null, role);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("관리자 권한 부여 성공")
    void grantAdminRoleSuccess() throws Exception {
        User user = createNormalUser("grantAdminRoleUser");
        User admin = createAdminUser("grantAdminRoleAdmin");
        setAuthenticationContext(admin);

        mvc.perform(MockMvcRequestBuilders.patch("/admin/users/{userId}/roles", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(UserRole.ADMIN, updatedUser.getRole());
    }

    @Test
    @DisplayName("관리자 권한 부여 실패 - 존재하지 않는 유저")
    void grantAdminRoleFail_UserNotFound() throws Exception {
        User admin = createAdminUser("grantAdminRoleFailAdmin");
        setAuthenticationContext(admin);

        Long nonExistentUserId = 999999L;
        mvc.perform(MockMvcRequestBuilders.patch("/admin/users/{userId}/roles", nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("관리자 권한 부여 실패 - 권한 없음")
    void grantAdminRoleFail_AccessDenied() throws Exception {
        User user = createNormalUser("grantAdminRoleFailUser");
        User normalUser = createNormalUser("grantAdminRoleFailNormalUser");
        setAuthenticationContext(normalUser);

        mvc.perform(MockMvcRequestBuilders.patch("/admin/users/{userId}/roles", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
