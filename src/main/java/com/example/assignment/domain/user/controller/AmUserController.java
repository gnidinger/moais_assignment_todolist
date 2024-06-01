package com.example.assignment.domain.user.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.assignment.domain.user.annotation.CurrentAmUser;
import com.example.assignment.domain.user.dto.AmUserDto;
import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.domain.user.service.AmUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AmUserController {

	private final AmUserService amUserService;

	@PostMapping("/register")
	@Operation(
		summary = "회원 가입",
		description = "사용자를 시스템에 등록합니다. 이메일 형식의 ID, 이름, 비밀번호 유효성을 검증합니다.",
		tags = {"User"},
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = AmUserDto.Register.class)
			)
		),
		responses = {
			@ApiResponse(
				responseCode = "201",
				description = "회원 가입 완료",
				content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = AmUserDto.RegisterResponse.class)
				)
			)
		}
	)
	public ResponseEntity<AmUserDto.RegisterResponse> registerUser(@Valid @RequestBody AmUserDto.Register registerDto) {
		log.info("회원 가입 요청 - ID: {}", registerDto.getId());
		AmUserDto.RegisterResponse response = amUserService.registerUser(registerDto);
		log.info("회원 가입 성공 - ID: {}", registerDto.getId());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/delete")
	@Operation(
		summary = "회원 탈퇴",
		description = "사용자의 비밀번호를 입력받아 회원 정보를 삭제합니다.",
		tags = {"User"},
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = AmUserDto.DeleteUserRequest.class)
			)
		),
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "회원 탈퇴 성공",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = AmUserDto.DeleteUserResponse.class)
				)
			)
		}
	)
	public ResponseEntity<AmUserDto.DeleteUserResponse> deleteUser(
		@CurrentAmUser AmUser currentAmUser,
		@Valid @RequestBody AmUserDto.DeleteUserRequest deleteUserRequest
	) {
		log.info("회원 탈퇴 요청 - 현재 사용자 ID: {}", currentAmUser.getId());
		AmUserDto.DeleteUserResponse response = amUserService.deleteUser(currentAmUser, deleteUserRequest);
		log.info("회원 탈퇴 성공 - ID: {}", currentAmUser.getId());
		return ResponseEntity.ok(response);
	}
}
