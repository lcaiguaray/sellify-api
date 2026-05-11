package com.sellify.api.common.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class FieldValidationException extends RuntimeException {
    
    private final Map<String, String> fieldErrors;

    public FieldValidationException(Map<String, String> fieldErrors) {
        super("Múltiples errores de validación de negocio");
        this.fieldErrors = fieldErrors;
    }

    public FieldValidationException(String field, String errorCode) {
        super(errorCode);
        this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(field, errorCode);
    }
}
