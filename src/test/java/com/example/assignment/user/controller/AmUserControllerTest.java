package com.example.assignment.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.example.assignment.domain.user.annotation.AmUserArgumentResolver;
import com.example.assignment.domain.user.controller.AmUserController;
import com.example.assignment.domain.user.dto.AmUserDto;
import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.domain.user.entity.enums.AuthType;
import com.example.assignment.domain.user.service.AmUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AmUserController.class)
@Import({AmUserArgumentResolver.class})
public class AmUserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AmUserArgumentResolver amUserArgumentResolver;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private AmUserService amUserService;

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
	public void registerUserTest() throws Exception {
		// Given
		AmUserDto.Register registerDto = AmUserDto.Register.builder()
			.id("user@example.com")
			.nickname("example-user")
			.password("password123")
			.passwordRepeat("password123")
			.build();

		AmUserDto.RegisterResponse registerResponse = new AmUserDto.RegisterResponse();
		registerResponse.setSeq(1L);
		registerResponse.setNickname("example-user");

		given(amUserService.registerUser(any(AmUserDto.Register.class))).willReturn(registerResponse);

		// When & Then
		mockMvc.perform(post("/api/user/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.seq").value(1))
			.andExpect(jsonPath("$.nickname").value("example-user"));
	}

	@Test
	@WithMockUser
	public void deleteUserTest() throws Exception {
		// Given
		AmUserDto.DeleteUserRequest deleteUserRequest = AmUserDto.DeleteUserRequest.builder()
			.password("password123")
			.build();

		AmUserDto.DeleteUserResponse deleteUserResponse = new AmUserDto.DeleteUserResponse();
		deleteUserResponse.setSeq(1L);
		deleteUserResponse.setNickname("example-user");

		given(amUserService.deleteUser(any(AmUser.class), any(AmUserDto.DeleteUserRequest.class))).willReturn(deleteUserResponse);

		// When & Then
		mockMvc.perform(delete("/api/user/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(deleteUserRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seq").value(1))
			.andExpect(jsonPath("$.nickname").value("example-user"));
	}
}
