package com.pr.sepp.utils.fasfdfs.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FdfsException extends RuntimeException {
    private int code;
    private String msg;

    public FdfsException() {

    }

    public FdfsException(int code, String message) {

    }
}
