package com.sellify.api.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final String messageKey;
    private final Object[] args;
    private final HttpStatus status;

    public BusinessException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = null;
        this.status = HttpStatus.NOT_FOUND;
    }

    public BusinessException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
        this.status = HttpStatus.NOT_FOUND;
    }

    public BusinessException(HttpStatus status, String messageKey, Object... args) {
        super(messageKey);
        this.status = status;
        this.messageKey = messageKey;
        this.args = args;
    }
}
