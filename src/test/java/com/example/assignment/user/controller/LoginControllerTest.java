package com.example.assignment.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.assignment.domain.user.service.AmUserService;
import com.example.assignment.domain.user.service.LoginService;
import com.example.assignment.global.config.security.dto.LoginRequestDto;
import com.example.assignment.global.config.security.dto.LoginResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LoginService loginService;

	@MockBean
	private AmUserService amUserService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.build();
	}

	@Test
	public void login() throws Exception {
		// given
		LoginRequestDto request = LoginRequestDto.builder()
			.id("user@example.com")
			.password("password123")
			.build();

		LoginResponseDto response = new LoginResponseDto();
		response.setSeq(1L);
		response.setNickname("username");

		String token = "Bearer sample.jwt.token";

		Mockito.when(loginService.login(Mockito.any(LoginRequestDto.class))).thenReturn(response);
		Mockito.when(loginService.createToken(Mockito.any(LoginRequestDto.class))).thenReturn(token);

		// when & then
		mockMvc.perform(post("/api/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(header().string(HttpHeaders.AUTHORIZATION, token))
			.andExpect(jsonPath("$.seq").value(1L))
			.andExpect(jsonPath("$.nickname").value("username"));
	}
}
