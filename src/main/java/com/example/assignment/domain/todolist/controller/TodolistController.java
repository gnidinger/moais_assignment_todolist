package com.example.assignment.domain.todolist.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.assignment.domain.todolist.dto.TodolistDto;
import com.example.assignment.domain.todolist.service.TodolistService;
import com.example.assignment.domain.user.annotation.CurrentAmUser;
import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.global.dto.response.MultiResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/todolist")
@RequiredArgsConstructor
public class TodolistController {

	private final TodolistService todolistService;

	@PostMapping()
	@Operation(
		summary = "Todolist 생성",
		description = "새로운 Todolist 항목을 생성합니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = TodolistDto.CreateTodolistRequest.class)
			)
		),
		responses = {
			@ApiResponse(
				responseCode = "201",
				description = "Todolist 생성 성공",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = TodolistDto.CreateTodolistResponse.class)
				)
			)
		}
	)
	public ResponseEntity<TodolistDto.CreateTodolistResponse> createTodolist(
		@CurrentAmUser AmUser currentUser,
		@Valid @RequestBody TodolistDto.CreateTodolistRequest createTodolistRequest) {

		log.info("Todolist 생성 요청 - 사용자 ID: {}, 제목: {}", currentUser.getId(), createTodolistRequest.getTitle());
		TodolistDto.CreateTodolistResponse response = todolistService.createTodolist(currentUser, createTodolistRequest);
		log.info("Todolist 생성 성공 - Todolist ID: {}, 제목: {}", response.getSeq(), response.getTitle());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/recent")
	@Operation(
		summary = "가장 최근의 Todolist 조회",
		description = "가장 최근에 작성된 Todolist 항목을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "가장 최근의 Todolist 조회 성공",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = TodolistDto.RetrieveTodolistResponse.class)
				)
			),
			@ApiResponse(
				responseCode = "204",
				description = "Todolist 항목 없음"
			)
		}
	)
	public ResponseEntity<TodolistDto.RetrieveTodolistResponse> getMostRecentTodolist(@CurrentAmUser AmUser currentUser) {
		log.info("가장 최근의 Todolist 조회 요청 - 사용자 ID: {}", currentUser.getId());
		TodolistDto.RetrieveTodolistResponse response = todolistService.retrieveMostRecentTodolist(currentUser);

		if (response == null) {
			log.info("가장 최근의 Todolist 항목 없음 - 사용자 ID: {}", currentUser.getId());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		log.info("가장 최근의 Todolist 조회 성공 - Todolist ID: {}, 제목: {}", response.getSeq(), response.getTitle());
		return ResponseEntity.ok(response);
	}

	@GetMapping
	@Operation(
		summary = "모든 Todolist 조회",
		description = "사용자의 모든 Todolist 항목을 조회합니다.",
		parameters = {
			@Parameter(name = "page", description = "페이지 번호 (기본값: 1)", required = false, schema = @Schema(type = "integer", defaultValue = "1")),
			@Parameter(name = "size", description = "페이지 크기 (기본값: 8)", required = false, schema = @Schema(type = "integer", defaultValue = "8"))
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "모든 Todolist 조회 성공",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = TodolistDto.ListTodolistResponse.class)
				)
			)
		}
	)
	public ResponseEntity<MultiResponseDto<TodolistDto.ListTodolistResponse>> getAllTodolists(
		@CurrentAmUser AmUser currentUser,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "8") int size) {
		log.info("모든 Todolist 조회 요청 - 사용자 ID: {}", currentUser.getId());
		MultiResponseDto<TodolistDto.ListTodolistResponse> response = todolistService.retrieveTodolists(currentUser, page, size);
		log.info("모든 Todolist 조회 성공 - 사용자 ID: {}, 항목 수: {}", currentUser.getId(), response.getData().size());

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{seq}/status")
	@Operation(
		summary = "Todolist 상태 업데이트",
		description = "Todolist 항목의 상태를 업데이트합니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = TodolistDto.UpdateTodolistStatusRequest.class)
			)
		),
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Todolist 상태 업데이트 성공",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = TodolistDto.UpdateTodolistStatusResponse.class)
				)
			)
		}
	)
	public ResponseEntity<TodolistDto.UpdateTodolistStatusResponse> updateTodolistStatus(
		@CurrentAmUser AmUser currentUser,
		@PathVariable Long seq,
		@Valid @RequestBody TodolistDto.UpdateTodolistStatusRequest updateTodolistStatusRequest) {

		log.info("Todolist 상태 업데이트 요청 - 사용자 ID: {}, Todolist ID: {}, 새로운 상태: {}",
			currentUser.getId(), seq, updateTodolistStatusRequest.getTodoStatus());

		TodolistDto.UpdateTodolistStatusResponse response = todolistService.updateTodolistStatus(currentUser, seq, updateTodolistStatusRequest);
		log.info("Todolist 상태 업데이트 성공 - Todolist ID: {}, 새로운 상태: {}", response.getSeq(), response.getStatus());

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{todolistSeq}")
	@Operation(
		summary = "Todolist 업데이트",
		description = "Todolist 항목을 업데이트합니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = TodolistDto.UpdateTodolistRequest.class)
			)
		),
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Todolist 업데이트 성공",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = TodolistDto.UpdateTodolistResponse.class)
				)
			)
		}
	)
	public ResponseEntity<TodolistDto.UpdateTodolistResponse> updateTodolist(
		@CurrentAmUser AmUser currentUser,
		@PathVariable Long todolistSeq,
		@Valid @RequestBody TodolistDto.UpdateTodolistRequest updateTodolistRequest) {

		log.info("Todolist 업데이트 요청 - 사용자 ID: {}, Todolist ID: {}, 제목: {}", currentUser.getId(), todolistSeq, updateTodolistRequest.getTitle());
		TodolistDto.UpdateTodolistResponse response = todolistService.updateTodolist(currentUser, todolistSeq, updateTodolistRequest);
		log.info("Todolist 업데이트 성공 - Todolist ID: {}, 제목: {}", response.getSeq(), response.getTitle());

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{todolistId}")
	@Operation(
		summary = "Todolist 삭제",
		description = "Todolist 항목을 삭제합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Todolist 삭제 성공",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = TodolistDto.DeleteTodolistResponse.class)
				)
			)
		}
	)
	public ResponseEntity<TodolistDto.DeleteTodolistResponse> deleteTodolist(
		@CurrentAmUser AmUser currentUser,
		@PathVariable Long todolistId) {

		log.info("Todolist 삭제 요청 - 사용자 ID: {}, Todolist ID: {}", currentUser.getId(), todolistId);
		TodolistDto.DeleteTodolistResponse response = todolistService.deleteTodolist(currentUser, todolistId);
		log.info("Todolist 삭제 성공 - Todolist ID: {}", response.getSeq());

		return ResponseEntity.ok(response);
	}
}
