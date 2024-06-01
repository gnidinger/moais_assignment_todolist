package com.example.assignment.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.domain.user.mapper.AmUserMapper;
import com.example.assignment.global.config.PBKDF2Encoder;
import com.example.assignment.global.config.security.dto.LoginRequestDto;
import com.example.assignment.global.config.security.dto.LoginResponseDto;
import com.example.assignment.global.config.security.filter.JwtTokenProvider;
import com.example.assignment.global.error.exception.BusinessLogicException;
import com.example.assignment.global.error.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

	private final AmUserMapper amUserMapper;
	private final AmUserService amUserService;
	private final PBKDF2Encoder pbkdf2Encoder;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
		log.info("로그인 요청 - ID: {}", loginRequestDto.getId());
		AmUser loginAmUser = amUserMapper.loginRequestToUser(loginRequestDto);
		AmUser findAmUser = amUserService.findById(loginAmUser.getId());

		if (!findAmUser.getPassword().equals(pbkdf2Encoder.encode(loginAmUser.getPassword()))) {
			log.error("비밀번호 불일치 - ID: {}", loginRequestDto.getId());
			throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
		}

		log.info("로그인 성공 - ID: {}", loginRequestDto.getId());
		return amUserMapper.ToLoginResponseDto(findAmUser);
	}

	public String createToken(LoginRequestDto loginRequestDto) {
		log.info("토큰 생성 요청 - ID: {}", loginRequestDto.getId());
		AmUser loginAmUser = amUserMapper.loginRequestToUser(loginRequestDto);
		String token = jwtTokenProvider.createToken(loginAmUser.getId());
		log.info("토큰 생성 성공 - ID: {}", loginRequestDto.getId());
		return token;
	}
}
