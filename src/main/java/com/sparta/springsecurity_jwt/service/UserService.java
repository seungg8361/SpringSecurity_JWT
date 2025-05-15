package com.sparta.springsecurity_jwt.service;

import com.sparta.springsecurity_jwt.common.exception.CustomException;
import com.sparta.springsecurity_jwt.common.exception.ResponseCode;
import com.sparta.springsecurity_jwt.domain.User;
import com.sparta.springsecurity_jwt.domain.UserRole;
import com.sparta.springsecurity_jwt.dto.request.LoginRequestDto;
import com.sparta.springsecurity_jwt.dto.request.SignUpRequestDto;
import com.sparta.springsecurity_jwt.dto.response.LoginResponseDto;
import com.sparta.springsecurity_jwt.dto.response.UserInfoResponseDto;
import com.sparta.springsecurity_jwt.repository.UserRepository;
import com.sparta.springsecurity_jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${admin.code}")
    private String adminCode;

    @Transactional
    public UserInfoResponseDto signUp(SignUpRequestDto requestDto) {
        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()){
            throw new CustomException(ResponseCode.USER_ALREADY_EXISTS);
        }

        String encodePassword = bCryptPasswordEncoder.encode(requestDto.getPassword());

        UserRole role = UserRole.USER;
        if (StringUtils.hasText(requestDto.getAdminCode())){
            if (!requestDto.getAdminCode().equals(adminCode)){
                throw new CustomException(ResponseCode.INVALID_CODE);
            }
            role = UserRole.ADMIN;
        }
        User user = User.create(
                requestDto.getUsername(), encodePassword, requestDto.getNickname(), role
        );

        userRepository.save(user);

        return UserInfoResponseDto.of(user);
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.username())
                .orElseThrow(()-> new CustomException(ResponseCode.USER_NOT_FOUND));

        if (!user.getUsername().equals(requestDto.username()) ||
                !bCryptPasswordEncoder.matches(requestDto.password(), user.getPassword())){
            throw new CustomException(ResponseCode.INVALID_CREDENTIALS);
        }

        String token = jwtUtil.createToken(user);

        return LoginResponseDto.of(token);
    }

    @Transactional
    public UserInfoResponseDto grantAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(ResponseCode.USER_NOT_FOUND));

        user.update(UserRole.ADMIN);

        return UserInfoResponseDto.of(user);
    }
}
