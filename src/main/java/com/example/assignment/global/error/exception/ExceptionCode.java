package com.example.assignment.global.error.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {

	INVALID_INPUT_VALUE(400, "Invalid Input Value"),
	INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
	HANDLE_ACCESS_DENIED(403, "Access Denied"),
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

	/* USER */
	USER_NOT_FOUND(404, "User Not Found"),
	EMAIL_EXIST(409, "Email Exists"),
	PASSWORD_MISMATCH(400, "Password Mismatch"),
	UNAUTHORIZED(401, "Unauthorized"), // 인증이 필요한 상태

	/* TODOLIST */
	INVALID_STATUS(400, "Invalid Status"),
	INVALID_TODOLIST_ID(400, "Invalid todolist ID"),
	INVALID_STATUS_TRANSITION(400, "Invalid status transition");

	private int status;
	private String message;

	ExceptionCode(int status, String message) {
		this.status = status;
		this.message = message;
	}
}

