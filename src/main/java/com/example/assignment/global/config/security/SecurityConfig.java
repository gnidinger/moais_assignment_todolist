package com.example.assignment.global.config.security;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.assignment.global.config.security.filter.JwtAuthenticationFilter;
import com.example.assignment.global.config.security.filter.JwtTokenProvider;
import com.example.assignment.global.error.ErrorResponse;
import com.example.assignment.global.error.exception.ExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;

	private static final String[] PUBLIC_ENDPOINTS = {
		"/h2/**"
	};

	private static final String[] PUBLIC_POST_ENDPOINTS = {
		"/api/user/register",
		"/api/login"
	};

	private static final String[] PUBLIC_GET_ENDPOINTS = {
		"/swagger-ui/**",
		"/v3/api-docs/**"
	};

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.headers().frameOptions().disable()
			.and()
			.cors().configurationSource(corsConfigurationSource())
			.and()
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling()
			.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
			.accessDeniedHandler(new CustomAccessDeniedHandler())
			.and()
			.authorizeRequests()
			.antMatchers(PUBLIC_ENDPOINTS).permitAll()
			.antMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()
			.antMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
			.anyRequest().authenticated()
			.and()
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return new CustomAuthenticationManager(jwtTokenProvider);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
		configuration.setExposedHeaders(Arrays.asList("Authorization", "Authorization-Refresh"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	private static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
		@Override
		public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
			Exception exception = (Exception)request.getAttribute("exception");
			ErrorResponse errorResponse = ErrorResponse.of(ExceptionCode.UNAUTHORIZED);

			response.setContentType("application/json;charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));

			logExceptionMessage(authException, exception);
		}

		private void logExceptionMessage(AuthenticationException authException, Exception exception) {
			String message = exception != null ? exception.getMessage() : authException.getMessage();
			log.error("Unauthorized error happened: {}", message);
		}
	}

	private static class CustomAccessDeniedHandler implements AccessDeniedHandler {
		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
			ErrorResponse errorResponse = ErrorResponse.of(ExceptionCode.HANDLE_ACCESS_DENIED);

			response.setContentType("application/json;charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));

			log.error("Forbidden error happened: {}", accessDeniedException.getMessage());
		}
	}

	@RequiredArgsConstructor
	public static class CustomAuthenticationManager implements AuthenticationManager {

		private final JwtTokenProvider jwtTokenProvider;  // JwtTokenProvider는 토큰을 검증하고 처리하는 클래스

		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {
			String authToken = authentication.getCredentials().toString();
			if (jwtTokenProvider.validateToken(authToken)) {
				String userId = jwtTokenProvider.getUserId(authToken);
				Authentication auth = jwtTokenProvider.getAuthentication(authToken);  // 토큰에서 Authentication 객체를 가져옴
				return auth;
			} else {
				throw new BadCredentialsException("Invalid token");
			}
		}
	}

	@RequiredArgsConstructor
	public static class CustomSecurityContextRepository implements SecurityContextRepository {

		private final AuthenticationManager authenticationManager;

		@Override
		@SuppressWarnings("deprecation")
		public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
			HttpServletRequest request = requestResponseHolder.getRequest();
			String authHeader = request.getHeader("Authorization");

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String authToken = authHeader.substring(7);
				Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
				Authentication authenticatedAuth = authenticationManager.authenticate(auth);
				SecurityContext securityContext = new SecurityContextImpl(authenticatedAuth);
				return securityContext;
			}

			return SecurityContextHolder.createEmptyContext();
		}

		@Override
		public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
			// 보안 컨텍스트를 저장하는 로직. 일반적으로 JWT 사용 시 사용하지 않음
		}

		@Override
		public boolean containsContext(HttpServletRequest request) {
			String authHeader = request.getHeader("Authorization");
			return authHeader != null && authHeader.startsWith("Bearer ");
		}
	}
}
