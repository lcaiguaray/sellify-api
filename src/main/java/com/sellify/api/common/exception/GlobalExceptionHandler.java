package com.sellify.api.common.exception;

import com.sellify.api.common.config.MessageTranslator;
import com.sellify.api.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageTranslator messageTranslator;

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<?> handleFieldValidationException(FieldValidationException ex) {
        Map<String, String> translatedErrors = new HashMap<>();
        
        ex.getFieldErrors().forEach((field, errorCode) -> {
            String translatedMessage = messageTranslator.getMessage(errorCode);
            translatedErrors.put(field, translatedMessage);
        });
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = messageTranslator.getMessage("error.validation");
        
        ApiResponse<?> apiResponse = ApiResponse.error(status.value(), translatedErrors, message);
        return ResponseEntity.status(status).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = messageTranslator.getMessage("error.validation");
        ApiResponse<?> apiResponse = ApiResponse.error(status.value(), errors, message);
        return ResponseEntity.status(status).body(apiResponse);
    }

    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    public ResponseEntity<?> handleBadCredentials(Exception ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = messageTranslator.getMessage("error.auth.credentials.invalid");
        ApiResponse<?> apiResponse = ApiResponse.error(status.value(), message);
        return ResponseEntity.status(status).body(apiResponse);
    }

    @ExceptionHandler({AuthenticationException.class, InsufficientAuthenticationException.class})
    public ResponseEntity<?> handleAuthenticationException(Exception ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = messageTranslator.getMessage("error.auth.unauthenticated");
        ApiResponse<?> apiResponse = ApiResponse.error(status.value(), message);
        return ResponseEntity.status(status).body(apiResponse);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<?> handleAccessDenied(Exception ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String message = messageTranslator.getMessage("error.auth.accessdenied");
        ApiResponse<?> apiResponse = ApiResponse.error(status.value(), message);
        return ResponseEntity.status(status).body(apiResponse);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex) {
        String message = messageTranslator.getMessage(ex.getMessageKey(), ex.getArgs());

        return ResponseEntity.status(ex.getStatus())
            .body(ApiResponse.error(ex.getStatus().value(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericExceptions(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = messageTranslator.getMessage("error.server.internal");
        ApiResponse<?> apiResponse = ApiResponse.error(status.value(), message);
        return ResponseEntity.status(status).body(apiResponse);
    }
}
