package com.sparta.springsecurity_jwt.controller;

import com.sparta.springsecurity_jwt.dto.request.LoginRequestDto;
import com.sparta.springsecurity_jwt.dto.request.SignUpRequestDto;
import com.sparta.springsecurity_jwt.dto.response.LoginResponseDto;
import com.sparta.springsecurity_jwt.dto.response.UserInfoResponseDto;
import com.sparta.springsecurity_jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users/signUp")
    public ResponseEntity<UserInfoResponseDto> signUp(@RequestBody SignUpRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.signUp(requestDto));

    }

    @PostMapping("/users/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {

        return ResponseEntity.ok()
                .body(userService.login(requestDto));
    }

//
//    @PatchMapping("/admin/users/{userId}/roles")
//    public ResponseEntity<UserInfoResponseDto> changeRole(@PathVariable("userId") Long userId) {
//
//        return ResponseEntity.ok()
//                .body(userService.changeRole(userId));
//    }
}
