package com.transport.tms.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Long id) {
        super("NOT_FOUND", resource + " not found: " + id);
    }

    public ResourceNotFoundException(String message) {
        super("NOT_FOUND", message);
    }
}
