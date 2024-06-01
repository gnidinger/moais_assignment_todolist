package com.example.assignment.global.config.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {

	@Schema(description = "사용자 식별자")
	private Long seq;

	@Schema(description = "사용자 닉네임")
	private String nickname;
}
