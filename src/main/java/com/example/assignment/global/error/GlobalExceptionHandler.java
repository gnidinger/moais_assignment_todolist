package com.example.assignment.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.assignment.global.error.exception.BusinessLogicException;
import com.example.assignment.global.error.exception.ExceptionCode;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        final ErrorResponse response = ErrorResponse.of(ExceptionCode.INVALID_INPUT_VALUE, e.getBindingResult());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BusinessLogicException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessLogicException e) {
        log.error("handleEntityNotFoundException", e);
        final ExceptionCode exceptionCode = e.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(exceptionCode);
        return new ResponseEntity<>(response, HttpStatus.valueOf(exceptionCode.getStatus()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDeniedException", e);
        final ErrorResponse response = ErrorResponse.of(ExceptionCode.HANDLE_ACCESS_DENIED);
        return new ResponseEntity<>(response, HttpStatus.valueOf(ExceptionCode.HANDLE_ACCESS_DENIED.getStatus()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        final ErrorResponse response = ErrorResponse.of(ExceptionCode.METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("handleHttpMessageNotReadableException", e);
        final ErrorResponse response = ErrorResponse.of(ExceptionCode.INVALID_INPUT_VALUE);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleEntityNotFoundException", e);
        final ErrorResponse response = ErrorResponse.of(ExceptionCode.INTERNAL_SERVER_ERROR);

        return ResponseEntity.internalServerError().body(response);
    }
}

