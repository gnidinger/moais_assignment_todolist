package com.example.assignment.domain.user.controller;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.assignment.domain.user.service.LoginService;
import com.example.assignment.global.config.security.dto.LoginRequestDto;
import com.example.assignment.global.config.security.dto.LoginResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

	private final LoginService loginService;

	@PostMapping("/login")
	@Operation(
		summary = "로그인",
		tags = {"Authentication"},
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = LoginRequestDto.class)
			)
		),
		responses = {
			@ApiResponse(
				responseCode = "200",
				headers = @Header(name = "Authorization", description = "JWT 토큰", schema = @Schema(type = "string")),
				content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = LoginResponseDto.class)
				)
			),
		}
	)
	public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
		log.info("로그인 요청 - ID: {}", loginRequestDto.getId());
		LoginResponseDto responseDto = loginService.login(loginRequestDto);
		String token = loginService.createToken(loginRequestDto); // 서비스에서 토큰 생성
		log.info("토큰 생성 성공 - ID: {}", loginRequestDto.getId());

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token); // 토큰을 헤더에 추가
		log.info("로그인 성공 - ID: {}", loginRequestDto.getId());

		return ResponseEntity.ok().headers(headers).body(responseDto);
	}
}
