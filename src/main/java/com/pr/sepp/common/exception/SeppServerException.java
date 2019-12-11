package com.pr.sepp.common.exception;

import org.springframework.http.HttpStatus;

public class SeppServerException extends SeppException {

    public SeppServerException(String message, Throwable throwable) {
        this(1001, message, throwable);
    }

    public SeppServerException(int code, String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), code, message);
    }

    public SeppServerException(int status, int code, String message) {
        super(status, code, message);
    }

    public SeppServerException(int code, String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), code, message, cause);
    }
}
