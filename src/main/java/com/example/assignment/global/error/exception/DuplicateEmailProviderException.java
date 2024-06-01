package com.example.assignment.global.error.exception;

import org.springframework.security.core.AuthenticationException;

public class DuplicateEmailProviderException extends AuthenticationException {
	public DuplicateEmailProviderException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DuplicateEmailProviderException(String msg) {
		super(msg);
	}
}
