package com.example.assignment.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.assignment.domain.todolist.entity.Todolist;
import com.example.assignment.domain.todolist.entity.enums.TodoStatus;
import com.example.assignment.domain.todolist.repository.TodolistRepository;
import com.example.assignment.global.scheduler.TodolistCleanupService;

@ExtendWith(MockitoExtension.class)
public class TodolistCleanupServiceTest {

	@Mock
	private TodolistRepository todolistRepository;

	@InjectMocks
	private TodolistCleanupService todolistCleanupService;

	@Captor
	private ArgumentCaptor<LocalDateTime> cutoffDateCaptor;

	@Test
	public void testCleanUpOldTodolists() {
		// Given
		LocalDateTime now = LocalDateTime.now();
		Todolist oldDoneTodo = Todolist.builder()
			.title("Old Done Task")
			.description("Old Done Description")
			.status(TodoStatus.DONE)
			.modifiedAt(now.minusWeeks(3))
			.build();

		List<Todolist> oldDoneTodos = List.of(oldDoneTodo);
		given(todolistRepository.findByStatusAndModifiedAtBefore(eq(TodoStatus.DONE), any(LocalDateTime.class))).willReturn(oldDoneTodos);

		// When
		todolistCleanupService.cleanUpOldTodolists();

		// Then
		verify(todolistRepository, times(1)).findByStatusAndModifiedAtBefore(eq(TodoStatus.DONE), cutoffDateCaptor.capture());
		verify(todolistRepository, times(1)).deleteAll(oldDoneTodos);

		// 확인
		LocalDateTime capturedCutoffDate = cutoffDateCaptor.getValue();
		assertEquals(now.minusWeeks(2).truncatedTo(ChronoUnit.SECONDS), capturedCutoffDate.truncatedTo(ChronoUnit.SECONDS));
	}
}
