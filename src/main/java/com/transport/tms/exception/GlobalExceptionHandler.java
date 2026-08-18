package com.transport.tms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("Access denied");
        problem.setDetail(ex.getMessage());
        problem.setProperty("code", "ACCESS_DENIED");
        return problem;
    }

    @ExceptionHandler({ResourceNotFoundException.class, jakarta.persistence.EntityNotFoundException.class})
    public ProblemDetail handleNotFound(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Resource not found");
        problem.setDetail(ex.getMessage());
        String code = ex instanceof ResourceNotFoundException ? ((ResourceNotFoundException) ex).getCode() : "NOT_FOUND";
        problem.setProperty("code", code);
        return problem;
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Business rule violation");
        problem.setDetail(ex.getMessage());
        problem.setProperty("code", ex.getCode());
        if (ex.getDetails() != null) {
            problem.setProperty("details", ex.getDetails());
        }
        return problem;
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ProblemDetail handleInvalidOperation(InvalidOperationException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid Operation");
        problem.setDetail(ex.getMessage());
        problem.setProperty("code", ex.getErrorCode() != null ? ex.getErrorCode().toString() : "INVALID_OPERATION");
        return problem;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Authentication failed");
        problem.setDetail("Invalid username or password");
        problem.setProperty("code", "INVALID_CREDENTIALS");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation failed");
        problem.setProperty("code", "VALIDATION_ERROR");
        problem.setProperty("errors", ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Unhandled Exception caught by GlobalExceptionHandler:", ex);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal server error");
        problem.setDetail(ex.getMessage());
        problem.setProperty("code", "INTERNAL_ERROR");
        return problem;
    }
}
