package com.example.assignment.annotation;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.stream.Collectors;
import java.util.List;

import com.example.assignment.global.config.security.userDetails.AuthUser;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

	@Override
	public SecurityContext createSecurityContext(WithCustomMockUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		List<SimpleGrantedAuthority> authorities = List.of(customUser.roles()).stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());

		AuthUser principal = AuthUser.of(customUser.id(), List.of(customUser.roles()));
		UsernamePasswordAuthenticationToken auth =
			new UsernamePasswordAuthenticationToken(principal, null, authorities);

		context.setAuthentication(auth);
		return context;
	}
}
