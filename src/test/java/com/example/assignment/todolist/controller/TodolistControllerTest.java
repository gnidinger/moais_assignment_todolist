package com.example.assignment.todolist.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.example.assignment.domain.todolist.controller.TodolistController;
import com.example.assignment.domain.todolist.dto.TodolistDto;
import com.example.assignment.domain.todolist.entity.enums.TodoStatus;
import com.example.assignment.domain.todolist.service.TodolistService;
import com.example.assignment.domain.user.annotation.AmUserArgumentResolver;
import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.domain.user.entity.enums.AuthType;
import com.example.assignment.domain.user.service.AmUserService;
import com.example.assignment.global.dto.response.MultiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@WithMockUser
@WebMvcTest(TodolistController.class)
@Import({AmUserArgumentResolver.class})
public class TodolistControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TodolistService todolistService;

	@MockBean
	private AmUserService amUserService;

	@MockBean
	private AmUserArgumentResolver amUserArgumentResolver;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	public void setup() {
		AmUser mockUser = AmUser.builder()
			.seq(1L)
			.nickname("example-user")
			.id("email@example.com")
			.authType(AuthType.ROLE_USER)
			.build();

		Mockito.when(amUserService.findById(Mockito.anyString())).thenReturn(mockUser);
		Mockito.when(amUserArgumentResolver.resolveArgument(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(mockUser);

		RequestMappingHandlerAdapter adapter = context.getBean(RequestMappingHandlerAdapter.class);
		adapter.setCustomArgumentResolvers(List.of(amUserArgumentResolver));

		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.build();
	}

	@Test
	public void createTodolist() throws Exception {
		// given
		TodolistDto.CreateTodolistRequest request = TodolistDto.CreateTodolistRequest.builder()
			.title("코드 작성")
			.description("새로운 기능을 위한 코드 작성")
			.build();

		TodolistDto.CreateTodolistResponse response = new TodolistDto.CreateTodolistResponse();
		response.setSeq(1L);
		response.setTitle("코드 작성");
		response.setDescription("새로운 기능을 위한 코드 작성");
		response.setStatus(TodoStatus.TODO.name());

		Mockito.when(todolistService.createTodolist(Mockito.any(AmUser.class), Mockito.any(TodolistDto.CreateTodolistRequest.class))).thenReturn(response);

		// when & then
		mockMvc.perform(post("/api/todolist")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.seq").value(1L))
			.andExpect(jsonPath("$.title").value("코드 작성"))
			.andExpect(jsonPath("$.description").value("새로운 기능을 위한 코드 작성"))
			.andExpect(jsonPath("$.status").value(TodoStatus.TODO.name()));
	}

	@Test
	public void getMostRecentTodolist() throws Exception {
		TodolistDto.RetrieveTodolistResponse response = new TodolistDto.RetrieveTodolistResponse();
		response.setSeq(1L);
		response.setTitle("코드 작성");
		response.setDescription("새로운 기능을 위한 코드 작성");
		response.setStatus(TodoStatus.TODO.name());

		Mockito.when(todolistService.retrieveMostRecentTodolist(Mockito.any(AmUser.class))).thenReturn(response);

		mockMvc.perform(get("/api/todolist/recent"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seq").value(1L))
			.andExpect(jsonPath("$.title").value("코드 작성"))
			.andExpect(jsonPath("$.description").value("새로운 기능을 위한 코드 작성"))
			.andExpect(jsonPath("$.status").value(TodoStatus.TODO.name()));
	}

	@Test
	public void getAllTodolists() throws Exception {
		TodolistDto.ListTodolistResponse listResponse = new TodolistDto.ListTodolistResponse();
		listResponse.setSeq(1L);
		listResponse.setTitle("코드 작성");
		listResponse.setStatus(TodoStatus.TODO.name());

		Page<TodolistDto.ListTodolistResponse> pageResponse = new PageImpl<>(
			Collections.singletonList(listResponse), PageRequest.of(0, 8), 1);

		MultiResponseDto<TodolistDto.ListTodolistResponse> multiResponse = new MultiResponseDto<>(
			pageResponse.getContent(), pageResponse);

		Mockito.when(todolistService.retrieveTodolists(Mockito.any(AmUser.class), Mockito.anyInt(), Mockito.anyInt()))
			.thenReturn(multiResponse);

		mockMvc.perform(get("/api/todolist")
				.param("page", "1")
				.param("size", "8"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].seq").value(1L))
			.andExpect(jsonPath("$.data[0].title").value("코드 작성"))
			.andExpect(jsonPath("$.data[0].status").value(TodoStatus.TODO.name()))
			.andExpect(jsonPath("$.pageInfo.page").value(1))
			.andExpect(jsonPath("$.pageInfo.size").value(8))
			.andExpect(jsonPath("$.pageInfo.totalElements").value(1))
			.andExpect(jsonPath("$.pageInfo.totalPages").value(1));
	}

	@Test
	public void updateTodolistStatus() throws Exception {
		Long seq = 1L;
		TodolistDto.UpdateTodolistStatusRequest request = TodolistDto.UpdateTodolistStatusRequest.builder()
			.todoStatus(TodoStatus.IN_PROGRESS)
			.build();

		TodolistDto.UpdateTodolistStatusResponse response = new TodolistDto.UpdateTodolistStatusResponse();
		response.setSeq(seq);
		response.setTitle("코드 작성");
		response.setDescription("새로운 기능을 위한 코드 작성");
		response.setStatus(TodoStatus.IN_PROGRESS.name());

		Mockito.when(todolistService.updateTodolistStatus(Mockito.any(AmUser.class), Mockito.eq(seq), Mockito.any(TodolistDto.UpdateTodolistStatusRequest.class)))
			.thenReturn(response);

		mockMvc.perform(patch("/api/todolist/{seq}/status", seq)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seq").value(seq))
			.andExpect(jsonPath("$.title").value("코드 작성"))
			.andExpect(jsonPath("$.description").value("새로운 기능을 위한 코드 작성"))
			.andExpect(jsonPath("$.status").value(TodoStatus.IN_PROGRESS.name()));
	}

	@Test
	public void updateTodolist() throws Exception {
		Long todolistSeq = 1L;
		TodolistDto.UpdateTodolistRequest request = TodolistDto.UpdateTodolistRequest.builder()
			.title("코드 작성 수정")
			.description("새로운 기능을 위한 코드 작성 수정")
			.build();

		TodolistDto.UpdateTodolistResponse response = new TodolistDto.UpdateTodolistResponse();
		response.setSeq(todolistSeq);
		response.setTitle("코드 작성 수정");
		response.setDescription("새로운 기능을 위한 코드 작성 수정");
		response.setStatus(TodoStatus.TODO.name());

		Mockito.when(todolistService.updateTodolist(Mockito.any(AmUser.class), Mockito.eq(todolistSeq), Mockito.any(TodolistDto.UpdateTodolistRequest.class)))
			.thenReturn(response);

		mockMvc.perform(patch("/api/todolist/{todolistSeq}", todolistSeq)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seq").value(todolistSeq))
			.andExpect(jsonPath("$.title").value("코드 작성 수정"))
			.andExpect(jsonPath("$.description").value("새로운 기능을 위한 코드 작성 수정"))
			.andExpect(jsonPath("$.status").value(TodoStatus.TODO.name()));
	}

	@Test
	public void deleteTodolist() throws Exception {
		Long todolistId = 1L;

		TodolistDto.DeleteTodolistResponse response = new TodolistDto.DeleteTodolistResponse();
		response.setSeq(todolistId);
		response.setTitle("삭제 제목");
		response.setDescription("삭제 설명");
		response.setStatus(TodoStatus.DONE.name());

		Mockito.when(todolistService.deleteTodolist(Mockito.any(AmUser.class), Mockito.eq(todolistId)))
			.thenReturn(response);

		mockMvc.perform(delete("/api/todolist/{todolistId}", todolistId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seq").value(todolistId))
			.andExpect(jsonPath("$.title").value("삭제 제목"))
			.andExpect(jsonPath("$.description").value("삭제 설명"))
			.andExpect(jsonPath("$.status").value(TodoStatus.DONE.name()));
	}
}
