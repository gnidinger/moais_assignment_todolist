package com.example.assignment.global.scheduler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/todolist-cleanup")
@RequiredArgsConstructor
public class TodolistCleanupController {

	private final TodolistCleanupService todolistCleanupService;

	@PostMapping
	public ResponseEntity<String> cleanUpOldTodolists() {
		todolistCleanupService.cleanUpOldTodolists();
		return ResponseEntity.ok("오래된 완료된 Todolist가 성공적으로 정리되었습니다.");
	}
}
