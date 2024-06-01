package com.example.assignment.global.error.exception;

public class BusinessLogicException extends RuntimeException{

    private ExceptionCode exceptionCode;

    public BusinessLogicException(String message, ExceptionCode exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public BusinessLogicException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getErrorCode() {
        return exceptionCode;
    }
}
