package com.example.assignment.domain.user.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.domain.user.service.AmUserService;
import com.example.assignment.global.config.security.userDetails.AuthUser;
import com.example.assignment.global.error.exception.BusinessLogicException;
import com.example.assignment.global.error.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmUserArgumentResolver implements HandlerMethodArgumentResolver {

	private final AmUserService amUserService;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		if (parameter == null) {
			return false;
		}
		return parameter.getParameterAnnotation(CurrentAmUser.class) != null
			&& parameter.getParameterType().equals(AmUser.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
		}

		Object principal = authentication.getPrincipal();
		if (!(principal instanceof AuthUser)) {
			throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
		}

		AuthUser authUser = (AuthUser) principal;
		return amUserService.findById(authUser.getId());
	}
}
