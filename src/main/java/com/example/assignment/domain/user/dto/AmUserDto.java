package com.example.assignment.domain.user.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AmUserDto {

	@Getter
	@Builder
	public static class Register {

		@NotBlank(message = "이메일을 입력하셔야 합니다.")
		@Schema(description = "사용자 이메일 주소. 이메일 형식을 따릅니다.", example = "user@example.com")
		@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "이메일 형식이 맞지 않습니다.")
		String id;

		@NotBlank(message = "사용자 닉네임을 입력하셔야 합니다.")
		@Schema(description = "사용자 이름. 2자 이상 10자 이하로 구성됩니다.", example = "example-user")
		@Pattern(regexp = "^[가-힣a-zA-Z0-9!@#$%^&*()-_+=<>?\\s]{2,20}$", message = "닉네임은 문자, 숫자, 특수문자(!@#$%^&*()-_+=<>?)를 포함한 2자 이상 20자 이하 여야 합니다.")
		String nickname;

		@NotBlank(message = "비밀번호를 입력하셔야 합니다.")
		@Schema(description = "사용자 비밀번호. 8자 이상이며, 최소 하나의 문자와 하나의 숫자를 포함해야 합니다.", example = "password123")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d\\W_]{8,15}$", message = "비밀번호는 숫자, 문자를 포함해 8자리 이상 15자리 이하 여야 합니다.")
		String password;

		@NotBlank(message = "확인을 위한 비밀번호를 입력하셔야 합니다.")
		@Schema(description = "비밀번호 확인을 위한 필드. 비밀번호와 동일한 값을 입력해야 합니다.", example = "password123")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d\\W_]{8,15}$", message = "비밀번호는 숫자, 문자를 포함해 8자리 이상 15자리 이하 여야 합니다.")
		String passwordRepeat;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DeleteUserRequest {

		@NotBlank(message = "비밀번호를 입력하셔야 합니다.")
		@Schema(description = "사용자의 비밀번호", example = "password123")
		private String password;
	}

	@Getter
	@Setter
	public static class RegisterResponse {

		@Schema(description = "사용자 식별자")
		Long seq;

		@Schema(description = "사용자 닉네임")
		String nickname;
	}

	@Getter
	@Setter
	public static class DeleteUserResponse {
		@Schema(description = "탈퇴한 사용자 식별자")
		private Long seq;

		@Schema(description = "탈퇴한 사용자 닉네임")
		private String nickname;
	}
}
