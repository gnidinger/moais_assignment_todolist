package com.example.assignment.domain.todolist.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.assignment.domain.todolist.entity.enums.TodoStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TodolistDto {

	@Getter
	@Builder
	public static class CreateTodolistRequest {

		@NotBlank(message = "제목을 입력하셔야 합니다.")
		@Schema(description = "TODO 제목. 2자 이상 20자 이하로 구성됩니다.", example = "코드 작성")
		@Size(min = 2, max = 20, message = "제목은 2자 이상 20자 이하 여야 합니다.")
		private String title;

		@Schema(description = "TODO 설명. 최대 255자까지 입력할 수 있습니다.", example = "새로운 기능을 위한 코드 작성")
		@Size(max = 255, message = "설명은 최대 255자까지 입력할 수 있습니다.")
		private String description;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UpdateTodolistStatusRequest {

		@NotNull(message = "상태를 입력하셔야 합니다.")
		@Schema(description = "TODO 상태", example = "IN_PROGRESS")
		private TodoStatus todoStatus;
	}

	@Getter
	@Builder
	public static class UpdateTodolistRequest {

		@NotBlank(message = "제목을 입력하셔야 합니다.")
		@Schema(description = "TODO 제목. 2자 이상 20자 이하로 구성됩니다.", example = "코드 작성")
		@Size(min = 2, max = 20, message = "제목은 2자 이상 20자 이하 여야 합니다.")
		private String title;

		@Schema(description = "TODO 설명. 최대 255자까지 입력할 수 있습니다.", example = "새로운 기능을 위한 코드 작성")
		@Size(max = 255, message = "설명은 최대 255자까지 입력할 수 있습니다.")
		private String description;
	}

	@Getter
	@Setter
	public static class CreateTodolistResponse {

		@Schema(description = "TODO ID")
		private Long seq;

		@Schema(description = "TODO 제목")
		private String title;

		@Schema(description = "TODO 설명")
		private String description;

		@Schema(description = "TODO 상태")
		private String status;
	}

	@Getter
	@Setter
	public static class RetrieveTodolistResponse {

		@Schema(description = "TODO ID")
		private Long seq;

		@Schema(description = "TODO 제목")
		private String title;

		@Schema(description = "TODO 설명")
		private String description;

		@Schema(description = "TODO 상태")
		private String status;
	}

	@Getter
	@Setter
	public static class ListTodolistResponse {

		@Schema(description = "TODO ID")
		private Long seq;

		@Schema(description = "TODO 제목")
		private String title;

		@Schema(description = "TODO 상태")
		private String status;
	}

	@Getter
	@Setter
	public static class UpdateTodolistStatusResponse {

		@Schema(description = "TODO ID")
		private Long seq;

		@Schema(description = "TODO 제목")
		private String title;

		@Schema(description = "TODO 설명")
		private String description;

		@Schema(description = "TODO 상태")
		private String status;
	}

	@Getter
	@Setter
	public static class UpdateTodolistResponse {

		@Schema(description = "TODO ID")
		private Long seq;

		@Schema(description = "TODO 제목")
		private String title;

		@Schema(description = "TODO 설명")
		private String description;

		@Schema(description = "TODO 상태")
		private String status;
	}

	@Getter
	@Setter
	public static class DeleteTodolistResponse {

		@Schema(description = "삭제된 TODO ID")
		private Long seq;

		@Schema(description = "삭제된 TODO 제목")
		private String title;

		@Schema(description = "삭제된 TODO 설명")
		private String description;

		@Schema(description = "삭제된 TODO 상태")
		private String status;
	}
}
