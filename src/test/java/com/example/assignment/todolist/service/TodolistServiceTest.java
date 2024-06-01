package com.example.assignment.todolist.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.assignment.domain.todolist.dto.TodolistDto;
import com.example.assignment.domain.todolist.entity.Todolist;
import com.example.assignment.domain.todolist.entity.enums.TodoStatus;
import com.example.assignment.domain.todolist.mapper.TodolistMapper;
import com.example.assignment.domain.todolist.repository.TodolistRepository;
import com.example.assignment.domain.todolist.service.TodolistService;
import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.domain.user.entity.enums.AuthType;
import com.example.assignment.global.error.exception.BusinessLogicException;
import com.example.assignment.global.error.exception.ExceptionCode;

@ExtendWith(MockitoExtension.class)
public class TodolistServiceTest {

	@Mock
	private TodolistMapper todolistMapper;

	@Mock
	private TodolistRepository todolistRepository;

	@InjectMocks
	private TodolistService todolistService;

	@Test
	public void createTodolist_Success() {
		// Given
		AmUser user = AmUser.builder()
			.id("user@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("example-user")
			.build();

		TodolistDto.CreateTodolistRequest createTodolistRequest = TodolistDto.CreateTodolistRequest.builder()
			.title("New Task")
			.description("Task Description")
			.build();

		Todolist todolist = Todolist.builder()
			.seq(1L)
			.title(createTodolistRequest.getTitle())
			.description(createTodolistRequest.getDescription())
			.user(user)
			.status(TodoStatus.TODO)
			.build();

		TodolistDto.CreateTodolistResponse expectedResponse = new TodolistDto.CreateTodolistResponse();
		expectedResponse.setSeq(todolist.getSeq());
		expectedResponse.setTitle(todolist.getTitle());
		expectedResponse.setDescription(todolist.getDescription());

		when(todolistRepository.save(any(Todolist.class))).thenReturn(todolist);
		when(todolistMapper.toCreateTodolistResponse(any(Todolist.class))).thenReturn(expectedResponse);

		// When
		TodolistDto.CreateTodolistResponse response = todolistService.createTodolist(user, createTodolistRequest);

		// Then
		assertNotNull(response);
		assertEquals(expectedResponse.getSeq(), response.getSeq());
		assertEquals(expectedResponse.getTitle(), response.getTitle());
		assertEquals(expectedResponse.getDescription(), response.getDescription());
		verify(todolistRepository, times(1)).save(any(Todolist.class));
		verify(todolistMapper, times(1)).toCreateTodolistResponse(any(Todolist.class));
	}

	@Test
	public void retrieveMostRecentTodolist_Success() {
		// Given
		AmUser user = AmUser.builder()
			.id("user@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("example-user")
			.build();

		Todolist todolist = Todolist.builder()
			.seq(1L)
			.title("Most Recent Task")
			.description("Task Description")
			.user(user)
			.status(TodoStatus.TODO)
			.build();

		TodolistDto.RetrieveTodolistResponse expectedResponse = new TodolistDto.RetrieveTodolistResponse();
		expectedResponse.setSeq(todolist.getSeq());
		expectedResponse.setTitle(todolist.getTitle());
		expectedResponse.setDescription(todolist.getDescription());

		when(todolistRepository.findFirstByUserOrderByCreatedAtDesc(user)).thenReturn(todolist);
		when(todolistMapper.toRetrieveTodolistResponse(any(Todolist.class))).thenReturn(expectedResponse);

		// When
		TodolistDto.RetrieveTodolistResponse response = todolistService.retrieveMostRecentTodolist(user);

		// Then
		assertNotNull(response);
		assertEquals(expectedResponse.getSeq(), response.getSeq());
		assertEquals(expectedResponse.getTitle(), response.getTitle());
		assertEquals(expectedResponse.getDescription(), response.getDescription());
		verify(todolistRepository, times(1)).findFirstByUserOrderByCreatedAtDesc(user);
		verify(todolistMapper, times(1)).toRetrieveTodolistResponse(any(Todolist.class));
	}

	@Test
	public void retrieveTodolists_Success() {
		// Given
		AmUser user = AmUser.builder()
			.id("user@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("example-user")
			.build();

		Todolist todolist1 = Todolist.builder()
			.seq(1L)
			.title("Task 1")
			.user(user)
			.status(TodoStatus.TODO)
			.build();

		Todolist todolist2 = Todolist.builder()
			.seq(2L)
			.title("Task 2")
			.user(user)
			.status(TodoStatus.TODO)
			.build();

		List<Todolist> todolists = List.of(todolist1, todolist2);

		TodolistDto.ListTodolistResponse response1 = new TodolistDto.ListTodolistResponse();
		response1.setSeq(todolist1.getSeq());
		response1.setTitle(todolist1.getTitle());

		TodolistDto.ListTodolistResponse response2 = new TodolistDto.ListTodolistResponse();
		response2.setSeq(todolist2.getSeq());
		response2.setTitle(todolist2.getTitle());

		List<TodolistDto.ListTodolistResponse> expectedResponses = List.of(response1, response2);

		when(todolistRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(todolists);
		when(todolistMapper.toRetrieveTodolistResponseList(anyList())).thenReturn(expectedResponses);

		// When
		List<TodolistDto.ListTodolistResponse> responses = todolistService.retrieveTodolists(user);

		// Then
		assertNotNull(responses);
		assertEquals(expectedResponses.size(), responses.size());
		assertEquals(expectedResponses.get(0).getSeq(), responses.get(0).getSeq());
		assertEquals(expectedResponses.get(0).getTitle(), responses.get(0).getTitle());
		verify(todolistRepository, times(1)).findByUserOrderByCreatedAtDesc(user);
		verify(todolistMapper, times(1)).toRetrieveTodolistResponseList(anyList());
	}

	@Test
	public void updateTodolistStatus_Success() {
		// Given
		AmUser user = AmUser.builder()
			.id("user@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("example-user")
			.build();

		Todolist todolist = Todolist.builder()
			.seq(1L)
			.title("Task")
			.user(user)
			.status(TodoStatus.TODO)
			.build();

		TodolistDto.UpdateTodolistStatusRequest updateRequest = TodolistDto.UpdateTodolistStatusRequest.builder()
			.todoStatus(TodoStatus.IN_PROGRESS)
			.build();

		TodolistDto.UpdateTodolistStatusResponse expectedResponse = new TodolistDto.UpdateTodolistStatusResponse();
		expectedResponse.setSeq(todolist.getSeq());
		expectedResponse.setTitle(todolist.getTitle());
		expectedResponse.setDescription(todolist.getDescription());
		expectedResponse.setStatus(updateRequest.getTodoStatus().name());

		when(todolistRepository.findById(any(Long.class))).thenReturn(Optional.of(todolist));
		when(todolistRepository.save(any(Todolist.class))).thenReturn(todolist);
		when(todolistMapper.toUpdateTodolistStatusResponse(any(Todolist.class))).thenReturn(expectedResponse);

		// When
		TodolistDto.UpdateTodolistStatusResponse response = todolistService.updateTodolistStatus(user, 1L, updateRequest);

		// Then
		assertNotNull(response);
		assertEquals(expectedResponse.getSeq(), response.getSeq());
		assertEquals(expectedResponse.getTitle(), response.getTitle());
		assertEquals(expectedResponse.getDescription(), response.getDescription());
		assertEquals(expectedResponse.getStatus(), response.getStatus());
		verify(todolistRepository, times(1)).findById(any(Long.class));
		verify(todolistRepository, times(1)).save(any(Todolist.class));
		verify(todolistMapper, times(1)).toUpdateTodolistStatusResponse(any(Todolist.class));
	}

	@Test
	public void updateTodolist_Success() {
		// Given
		AmUser user = AmUser.builder()
			.id("user@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("example-user")
			.build();

		Todolist todolist = Todolist.builder()
			.seq(1L)
			.title("Task")
			.user(user)
			.status(TodoStatus.TODO)
			.build();

		TodolistDto.UpdateTodolistRequest updateRequest = TodolistDto.UpdateTodolistRequest.builder()
			.title("Updated Task")
			.description("Updated Description")
			.build();

		TodolistDto.UpdateTodolistResponse expectedResponse = new TodolistDto.UpdateTodolistResponse();
		expectedResponse.setSeq(todolist.getSeq());
		expectedResponse.setTitle(updateRequest.getTitle());
		expectedResponse.setDescription(updateRequest.getDescription());
		expectedResponse.setStatus(todolist.getStatus().name());

		when(todolistRepository.findById(any(Long.class))).thenReturn(Optional.of(todolist));
		when(todolistRepository.save(any(Todolist.class))).thenReturn(todolist);
		when(todolistMapper.toUpdateTodolistResponse(any(Todolist.class))).thenReturn(expectedResponse);

		// When
		TodolistDto.UpdateTodolistResponse response = todolistService.updateTodolist(user, 1L, updateRequest);

		// Then
		assertNotNull(response);
		assertEquals(expectedResponse.getSeq(), response.getSeq());
		assertEquals(expectedResponse.getTitle(), response.getTitle());
		assertEquals(expectedResponse.getDescription(), response.getDescription());
		assertEquals(expectedResponse.getStatus(), response.getStatus());
		verify(todolistRepository, times(1)).findById(any(Long.class));
		verify(todolistRepository, times(1)).save(any(Todolist.class));
		verify(todolistMapper, times(1)).toUpdateTodolistResponse(any(Todolist.class));
	}

	@Test
	public void deleteTodolist_Success() {
		// Given
		AmUser user = AmUser.builder()
			.id("user@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("example-user")
			.build();

		Todolist todolist = Todolist.builder()
			.seq(1L)
			.title("Task")
			.user(user)
			.status(TodoStatus.TODO)
			.build();

		TodolistDto.DeleteTodolistResponse expectedResponse = new TodolistDto.DeleteTodolistResponse();
		expectedResponse.setSeq(todolist.getSeq());
		expectedResponse.setTitle(todolist.getTitle());
		expectedResponse.setDescription(todolist.getDescription());
		expectedResponse.setStatus(todolist.getStatus().name());

		when(todolistRepository.findById(any(Long.class))).thenReturn(Optional.of(todolist));
		doNothing().when(todolistRepository).delete(any(Todolist.class));
		when(todolistMapper.toDeleteTodolistResponse(any(Todolist.class))).thenReturn(expectedResponse);

		// When
		TodolistDto.DeleteTodolistResponse response = todolistService.deleteTodolist(user, 1L);

		// Then
		assertNotNull(response);
		assertEquals(expectedResponse.getSeq(), response.getSeq());
		assertEquals(expectedResponse.getTitle(), response.getTitle());
		assertEquals(expectedResponse.getDescription(), response.getDescription());
		assertEquals(expectedResponse.getStatus(), response.getStatus());
		verify(todolistRepository, times(1)).findById(any(Long.class));
		verify(todolistRepository, times(1)).delete(any(Todolist.class));
		verify(todolistMapper, times(1)).toDeleteTodolistResponse(any(Todolist.class));
	}

	@Test
	public void updateTodolistStatus_Unauthorized() {
		// Given
		AmUser user = AmUser.builder()
			.id("user@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("example-user")
			.build();

		AmUser anotherUser = AmUser.builder()
			.id("another@example.com")
			.build();

		Todolist todolist = Todolist.builder()
			.seq(1L)
			.title("Task")
			.user(anotherUser)
			.status(TodoStatus.TODO)
			.build();

		TodolistDto.UpdateTodolistStatusRequest updateRequest = TodolistDto.UpdateTodolistStatusRequest.builder()
			.todoStatus(TodoStatus.IN_PROGRESS)
			.build();

		when(todolistRepository.findById(any(Long.class))).thenReturn(Optional.of(todolist));

		// When & Then
		BusinessLogicException exception = assertThrows(BusinessLogicException.class, () ->
			todolistService.updateTodolistStatus(user, 1L, updateRequest)
		);

		assertEquals(ExceptionCode.UNAUTHORIZED, exception.getErrorCode());
		verify(todolistRepository, times(1)).findById(any(Long.class));
		verify(todolistRepository, times(0)).save(any(Todolist.class));
		verify(todolistMapper, times(0)).toUpdateTodolistStatusResponse(any(Todolist.class));
	}

	@Test
	public void deleteTodolist_Unauthorized() {
		// Given
		AmUser user = AmUser.builder()
			.id("user@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("example-user")
			.build();

		AmUser anotherUser = AmUser.builder()
			.id("another@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("another-user")
			.build();

		Todolist todolist = Todolist.builder()
			.seq(1L)
			.title("Task")
			.user(anotherUser)
			.status(TodoStatus.TODO)
			.build();

		when(todolistRepository.findById(any(Long.class))).thenReturn(Optional.of(todolist));

		// When & Then
		BusinessLogicException exception = assertThrows(BusinessLogicException.class, () ->
			todolistService.deleteTodolist(user, 1L)
		);

		assertEquals(ExceptionCode.UNAUTHORIZED, exception.getErrorCode());
		verify(todolistRepository, times(1)).findById(any(Long.class));
		verify(todolistRepository, times(0)).delete(any(Todolist.class));
		verify(todolistMapper, times(0)).toDeleteTodolistResponse(any(Todolist.class));
	}
}
