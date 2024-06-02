package com.example.assignment.todolist.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.assignment.domain.todolist.entity.Todolist;
import com.example.assignment.domain.todolist.entity.enums.TodoStatus;
import com.example.assignment.domain.todolist.repository.TodolistRepository;
import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.domain.user.entity.enums.AuthType;
import com.example.assignment.domain.user.repository.AmUserRepository;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class TodolistRepositoryTest {

	@Autowired
	private TodolistRepository todolistRepository;

	@Autowired
	private AmUserRepository amUserRepository;

	@Test
	public void testFindFirstByUserOrderByCreatedAtDesc() {
		// Given
		AmUser user = AmUser.builder()
			.id("user@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("example-user")
			.build();

		amUserRepository.save(user);

		Todolist todolist1 = Todolist.builder()
			.title("Task 1")
			.description("Description 1")
			.status(TodoStatus.PENDING)
			.user(user)
			.build();

		Todolist todolist2 = Todolist.builder()
			.title("Task 2")
			.description("Description 2")
			.status(TodoStatus.PENDING)
			.user(user)
			.build();

		todolistRepository.save(todolist1);
		todolistRepository.save(todolist2);

		// When
		Todolist foundTodolist = todolistRepository.findFirstByUserOrderByCreatedAtDesc(user);

		// Then
		assertNotNull(foundTodolist);
		assertEquals(todolist2.getTitle(), foundTodolist.getTitle());
	}

	@Test
	public void testFindByUserOrderByCreatedAtDesc() {
		// Given
		AmUser user = AmUser.builder()
			.id("user@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("example-user")
			.build();

		amUserRepository.save(user);

		Todolist todolist1 = Todolist.builder()
			.title("Task 1")
			.description("Description 1")
			.status(TodoStatus.PENDING)
			.user(user)
			.build();

		Todolist todolist2 = Todolist.builder()
			.title("Task 2")
			.description("Description 2")
			.status(TodoStatus.PENDING)
			.user(user)
			.build();

		todolistRepository.save(todolist1);
		todolistRepository.save(todolist2);

		// When
		PageRequest pageRequest = PageRequest.of(0, 8); // 페이지 번호 0, 크기 8
		Page<Todolist> todolistPage = todolistRepository.findByUserOrderByCreatedAtDesc(user, pageRequest);

		// Then
		assertNotNull(todolistPage);
		assertEquals(2, todolistPage.getTotalElements());
		List<Todolist> todolists = todolistPage.getContent();
		assertEquals(todolist2.getTitle(), todolists.get(0).getTitle());
		assertEquals(todolist1.getTitle(), todolists.get(1).getTitle());
	}

	@Test
	public void testFindByStatusAndModifiedAtBefore() {
		// Given
		AmUser user = AmUser.builder()
			.id("user@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("example-user")
			.build();

		amUserRepository.save(user);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime threeWeeksAgo = now.minusWeeks(3);
		LocalDateTime oneWeekAgo = now.minusWeeks(1);

		Todolist oldDoneTodo = Todolist.builder()
			.title("Old Done Task")
			.description("Old Done Description")
			.status(TodoStatus.DONE)
			.modifiedAt(threeWeeksAgo)
			.user(user)
			.build();

		Todolist recentDoneTodo = Todolist.builder()
			.title("Recent Done Task")
			.description("Recent Done Description")
			.status(TodoStatus.DONE)
			.modifiedAt(oneWeekAgo)
			.user(user)
			.build();

		todolistRepository.save(oldDoneTodo);
		todolistRepository.save(recentDoneTodo);

		// When
		LocalDateTime cutoffDate = LocalDateTime.now().minusWeeks(2);
		List<Todolist> oldDoneTodos = todolistRepository.findByStatusAndModifiedAtBefore(TodoStatus.DONE, cutoffDate);

		// Then
		assertNotNull(oldDoneTodos);
		assertEquals(1, oldDoneTodos.size());
		assertEquals(oldDoneTodo.getTitle(), oldDoneTodos.get(0).getTitle());
	}
}
