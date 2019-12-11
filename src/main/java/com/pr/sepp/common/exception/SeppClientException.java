package com.pr.sepp.common.exception;

import org.springframework.http.HttpStatus;

public class SeppClientException extends SeppException {

    public SeppClientException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), 1001, message);
    }

    public SeppClientException(int code, String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), code, message);
    }

    public SeppClientException(int status, int code, String message) {
        super(status, code, message);
    }

    public SeppClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
