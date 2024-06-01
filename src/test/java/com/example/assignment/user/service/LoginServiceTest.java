package com.example.assignment.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.domain.user.mapper.AmUserMapper;
import com.example.assignment.domain.user.service.AmUserService;
import com.example.assignment.domain.user.service.LoginService;
import com.example.assignment.global.config.PBKDF2Encoder;
import com.example.assignment.global.config.security.dto.LoginRequestDto;
import com.example.assignment.global.config.security.dto.LoginResponseDto;
import com.example.assignment.global.config.security.filter.JwtTokenProvider;
import com.example.assignment.global.error.exception.BusinessLogicException;
import com.example.assignment.global.error.exception.ExceptionCode;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

	@Mock
	private AmUserMapper amUserMapper;

	@Mock
	private AmUserService amUserService;

	@Mock
	private PBKDF2Encoder pbkdf2Encoder;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@InjectMocks
	private LoginService loginService;

	@Test
	public void login_Success() {
		// Given
		LoginRequestDto loginRequestDto = LoginRequestDto.builder()
			.id("user@example.com")
			.password("password123")
			.build();

		AmUser loginAmUser = AmUser.builder()
			.id("user@example.com")
			.password("password123")
			.build();

		AmUser findAmUser = AmUser.builder()
			.id("user@example.com")
			.password("encodedpassword123")
			.build();

		LoginResponseDto expectedResponse = new LoginResponseDto();
		expectedResponse.setSeq(1L);
		expectedResponse.setNickname("username");

		when(amUserMapper.loginRequestToUser(any(LoginRequestDto.class))).thenReturn(loginAmUser);
		when(amUserService.findById(any(String.class))).thenReturn(findAmUser);
		when(pbkdf2Encoder.encode(anyString())).thenReturn("encodedpassword123");
		when(amUserMapper.ToLoginResponseDto(any(AmUser.class))).thenReturn(expectedResponse);

		// When
		LoginResponseDto response = loginService.login(loginRequestDto);

		// Then
		assertNotNull(response);
		verify(amUserMapper, times(1)).loginRequestToUser(any(LoginRequestDto.class));
		verify(amUserService, times(1)).findById(any(String.class));
		verify(pbkdf2Encoder, times(1)).encode(anyString());
		verify(amUserMapper, times(1)).ToLoginResponseDto(any(AmUser.class));

		assertEquals(expectedResponse.getSeq(), response.getSeq());
		assertEquals(expectedResponse.getNickname(), response.getNickname());
	}

	@Test
	public void login_Failure_InvalidPassword() {
		// Given
		LoginRequestDto loginRequestDto = LoginRequestDto.builder()
			.id("user@example.com")
			.password("password123")
			.build();

		AmUser loginAmUser = AmUser.builder()
			.id("user@example.com")
			.password("password123")
			.build();

		AmUser findAmUser = AmUser.builder()
			.id("user@example.com")
			.password("encodedpassword123")
			.build();

		when(amUserMapper.loginRequestToUser(any(LoginRequestDto.class))).thenReturn(loginAmUser);
		when(amUserService.findById(any(String.class))).thenReturn(findAmUser);
		when(pbkdf2Encoder.encode(anyString())).thenReturn("differentencodedpassword");

		// When & Then
		BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
			loginService.login(loginRequestDto);
		});

		assertEquals(ExceptionCode.UNAUTHORIZED, exception.getErrorCode());
		verify(amUserMapper, times(1)).loginRequestToUser(any(LoginRequestDto.class));
		verify(amUserService, times(1)).findById(any(String.class));
		verify(pbkdf2Encoder, times(1)).encode(anyString());
		verify(amUserMapper, times(0)).ToLoginResponseDto(any(AmUser.class));
	}

	@Test
	public void createToken_Success() {
		// Given
		LoginRequestDto loginRequestDto = LoginRequestDto.builder()
			.id("user@example.com")
			.password("password123")
			.build();

		AmUser loginAmUser = AmUser.builder()
			.id("user@example.com")
			.password("password123")
			.build();

		String expectedToken = "jwt-token";

		when(amUserMapper.loginRequestToUser(any(LoginRequestDto.class))).thenReturn(loginAmUser);
		when(jwtTokenProvider.createToken(anyString())).thenReturn(expectedToken);

		// When
		String token = loginService.createToken(loginRequestDto);

		// Then
		assertNotNull(token);
		assertEquals(expectedToken, token);
		verify(amUserMapper, times(1)).loginRequestToUser(any(LoginRequestDto.class));
		verify(jwtTokenProvider, times(1)).createToken(anyString());
	}
}

