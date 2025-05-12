package com.sparta.springsecurity_jwt.service;

import com.sparta.springsecurity_jwt.common.exception.CustomException;
import com.sparta.springsecurity_jwt.common.exception.ResponseCode;
import com.sparta.springsecurity_jwt.domain.User;
import com.sparta.springsecurity_jwt.dto.request.LoginRequestDto;
import com.sparta.springsecurity_jwt.dto.request.SignUpRequestDto;
import com.sparta.springsecurity_jwt.dto.response.LoginResponseDto;
import com.sparta.springsecurity_jwt.dto.response.UserInfoResponseDto;
import com.sparta.springsecurity_jwt.repository.UserRepository;
import com.sparta.springsecurity_jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserInfoResponseDto signUp(SignUpRequestDto requestDto) {
        if (userRepository.findByUsername(requestDto.username()).isPresent()){
            throw new CustomException(ResponseCode.USER_ALREADY_EXISTS);
        }

        String encodePassword = bCryptPasswordEncoder.encode(requestDto.password());

        User user = User.create(
                requestDto.username(), encodePassword, requestDto.nickname()
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

//    @Transactional
//    public UserInfoResponseDto changeRole(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(()-> new CustomException(ResponseCode.USER_NOT_FOUND));
//
//        if (user.getRole())
//    }
}
