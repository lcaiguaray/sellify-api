package com.sellify.api.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String messageKey) {
        super(HttpStatus.NOT_FOUND, messageKey);
    }

}
