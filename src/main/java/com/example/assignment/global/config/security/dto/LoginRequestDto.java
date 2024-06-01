package com.example.assignment.global.config.security.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

	@NotBlank(message = "아이디를 입력하셔야 합니다.")
	@Schema(description = "사용자의 이메일 주소. 이메일 형식을 따릅니다.", example = "user@example.com")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "이메일 형식이 맞지 않습니다.")
	private String id;

	@NotBlank(message = "패스워드를 입력하셔야 합니다.")
	@Schema(description = "사용자의 비밀번호. 8자 이상이며, 최소 하나의 문자와 하나의 숫자를 포함해야 합니다.", example = "password123")
	private String password;
}
