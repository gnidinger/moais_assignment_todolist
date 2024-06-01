package com.example.assignment.global.scheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.assignment.domain.todolist.entity.Todolist;
import com.example.assignment.domain.todolist.entity.enums.TodoStatus;
import com.example.assignment.domain.todolist.repository.TodolistRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodolistCleanupService {

	private final TodolistRepository todolistRepository;

	@Transactional
	@Scheduled(cron = "0 0 0 * * *") // 매일 자정 실행
	public void cleanUpOldTodolists() {
		LocalDateTime cutoffDate = LocalDateTime.now().minusWeeks(2);
		log.info("삭제할 오래된 Todolist 목록의 기준 날짜: {}", cutoffDate);

		List<Todolist> oldDoneTodos = todolistRepository.findByStatusAndModifiedAtBefore(TodoStatus.DONE, cutoffDate);
		log.info("삭제할 Todolist: {}", oldDoneTodos);

		todolistRepository.deleteAll(oldDoneTodos);
		log.info("오래된 Todolist 목록을 삭제했습니다: {}", cutoffDate);
	}
}
