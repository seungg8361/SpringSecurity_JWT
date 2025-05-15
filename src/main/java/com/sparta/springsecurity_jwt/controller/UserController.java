package com.sparta.springsecurity_jwt.controller;

import com.sparta.springsecurity_jwt.dto.request.LoginRequestDto;
import com.sparta.springsecurity_jwt.dto.request.SignUpRequestDto;
import com.sparta.springsecurity_jwt.dto.response.LoginResponseDto;
import com.sparta.springsecurity_jwt.dto.response.UserInfoResponseDto;
import com.sparta.springsecurity_jwt.service.UserService;
import com.sparta.springsecurity_jwt.service.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerDocs{

    private final UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<UserInfoResponseDto> signUp(@Valid @RequestBody SignUpRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.signUp(requestDto));

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {

        return ResponseEntity.ok()
                .body(userService.login(requestDto));
    }

    @PatchMapping("/admin/users/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoResponseDto> grantAdminRole(@PathVariable("userId") Long userId,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok()
                .body(userService.grantAdminRole(userId));
    }
}
