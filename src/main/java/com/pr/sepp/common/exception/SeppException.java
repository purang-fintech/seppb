package com.pr.sepp.common.exception;

import lombok.Data;

@Data
public class SeppException extends RuntimeException {

    private int code;
    private int status;
    private String message;

    public SeppException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SeppException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public SeppException(int status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public SeppException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public SeppException(int status, int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
