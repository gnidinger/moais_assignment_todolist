package com.example.assignment.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.assignment.domain.user.dto.AmUserDto;
import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.domain.user.entity.enums.AuthType;
import com.example.assignment.domain.user.mapper.AmUserMapper;
import com.example.assignment.domain.user.repository.AmUserRepository;
import com.example.assignment.domain.user.service.AmUserService;
import com.example.assignment.global.config.PBKDF2Encoder;
import com.example.assignment.global.error.exception.BusinessLogicException;
import com.example.assignment.global.error.exception.ExceptionCode;

@ExtendWith(MockitoExtension.class)
public class AmUserServiceTest {

	@Mock
	private AmUserMapper amUserMapper;

	@Mock
	private PBKDF2Encoder pbkdf2Encoder;

	@Mock
	private AmUserRepository amUserRepository;

	@InjectMocks
	private AmUserService amUserService;

	@Test
	public void registerUser_Success() {
		// Given
		AmUserDto.Register registerDto = AmUserDto.Register.builder()
			.id("user@example.com")
			.nickname("example-user")
			.password("password123")
			.passwordRepeat("password123")
			.build();

		AmUser amUser = AmUser.builder()
			.id(registerDto.getId())
			.nickname(registerDto.getNickname())
			.password(registerDto.getPassword())
			.authType(AuthType.ROLE_USER)
			.build();

		AmUserDto.RegisterResponse expectedResponse = new AmUserDto.RegisterResponse();
		expectedResponse.setSeq(1L);
		expectedResponse.setNickname("example-user");

		when(amUserRepository.save(any(AmUser.class))).thenReturn(amUser);
		when(amUserMapper.ToRegisterResponse(any(AmUser.class))).thenReturn(expectedResponse);

		// When
		AmUserDto.RegisterResponse response = amUserService.registerUser(registerDto);

		// Then
		assertNotNull(response);
		verify(amUserRepository, times(1)).save(any(AmUser.class));
		verify(amUserMapper, times(1)).ToRegisterResponse(any(AmUser.class));

		assertEquals(expectedResponse.getSeq(), response.getSeq());
		assertEquals(expectedResponse.getNickname(), response.getNickname());
	}

	@Test
	public void registerUser_Failure_PasswordMismatch() {
		// Given
		AmUserDto.Register registerDto = AmUserDto.Register.builder()
			.id("user@example.com")
			.nickname("example-user")
			.password("password123")
			.passwordRepeat("differentpassword")
			.build();

		// When & Then
		BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> amUserService.registerUser(registerDto));
		assertEquals(ExceptionCode.PASSWORD_MISMATCH, exception.getErrorCode());
	}

	@Test
	public void deleteUser_Success() {
		// Given
		AmUser currentUser = AmUser.builder()
			.id("user@example.com")
			.password("encodedpassword")
			.build();

		AmUserDto.DeleteUserRequest deleteUserRequest = AmUserDto.DeleteUserRequest.builder()
			.password("password123")
			.build();

		AmUserDto.DeleteUserResponse expectedResponse = new AmUserDto.DeleteUserResponse();
		expectedResponse.setSeq(1L);
		expectedResponse.setNickname("example-user");

		when(pbkdf2Encoder.encode(anyString())).thenReturn("encodedpassword");
		when(amUserMapper.ToDeleteUserResponse(any(AmUser.class))).thenReturn(expectedResponse);

		// When
		AmUserDto.DeleteUserResponse response = amUserService.deleteUser(currentUser, deleteUserRequest);

		// Then
		assertNotNull(response);
		verify(amUserRepository, times(1)).delete(any(AmUser.class));
		verify(amUserMapper, times(1)).ToDeleteUserResponse(any(AmUser.class));

		assertEquals(expectedResponse.getSeq(), response.getSeq());
		assertEquals(expectedResponse.getNickname(), response.getNickname());
	}

	@Test
	public void deleteUser_Failure_Unauthorized() {
		// Given
		AmUser currentUser = AmUser.builder()
			.id("user@example.com")
			.password("encodedpassword")
			.build();

		AmUserDto.DeleteUserRequest deleteUserRequest = AmUserDto.DeleteUserRequest.builder()
			.password("wrongpassword")
			.build();

		when(pbkdf2Encoder.encode(anyString())).thenReturn("wrongencodedpassword");

		// When & Then
		BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> amUserService.deleteUser(currentUser, deleteUserRequest));
		assertEquals(ExceptionCode.UNAUTHORIZED, exception.getErrorCode());
	}

	@Test
	public void verifyExistId_ThrowsException_WhenIdExists() {
		// Given
		String userId = "user@example.com";
		when(amUserRepository.findById(userId)).thenReturn(Optional.of(AmUser.builder().id(userId).build()));

		// When & Then
		BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> amUserService.verifyExistId(userId));
		assertEquals(ExceptionCode.EMAIL_EXIST, exception.getErrorCode());
	}

	@Test
	public void verifyExistId_DoesNotThrowException_WhenIdDoesNotExist() {
		// Given
		String userId = "user@example.com";
		when(amUserRepository.findById(userId)).thenReturn(Optional.empty());

		// When & Then
		assertDoesNotThrow(() -> amUserService.verifyExistId(userId));
	}

	@Test
	public void findById_ReturnsUser_WhenUserExists() {
		// Given
		String userId = "user@example.com";
		AmUser user = AmUser.builder().id(userId).build();

		when(amUserRepository.findById(userId)).thenReturn(Optional.of(user));

		// When
		AmUser foundUser = amUserService.findById(userId);

		// Then
		assertNotNull(foundUser);
		assertEquals(userId, foundUser.getId());
		verify(amUserRepository, times(1)).findById(userId);
	}

	@Test
	public void findById_ThrowsException_WhenUserDoesNotExist() {
		// Given
		String userId = "user@example.com";
		when(amUserRepository.findById(userId)).thenReturn(Optional.empty());

		// When & Then
		BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> amUserService.findById(userId));
		assertEquals(ExceptionCode.USER_NOT_FOUND, exception.getErrorCode());
	}
}
