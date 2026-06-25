package com.transport.tms.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final Object details;

    public BusinessException(String code) {
        super(code);
        this.code = code;
        this.details = null;
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.details = null;
    }

    public BusinessException(String code, String message, Object details) {
        super(message);
        this.code = code;
        this.details = details;
    }
}
