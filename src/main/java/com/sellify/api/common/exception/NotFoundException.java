package com.sellify.api.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException {

    public NotFoundException(String messageKey, Object... args) {
        super(HttpStatus.UNAUTHORIZED, messageKey, args);
    }
    
    public NotFoundException(String messageKey) {
        super(HttpStatus.UNAUTHORIZED, messageKey);
    }
}
