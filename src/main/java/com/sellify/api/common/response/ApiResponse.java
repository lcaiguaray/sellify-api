package com.sellify.api.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

import org.springframework.http.HttpStatus;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;
    
    @Builder.Default
    private Instant timestamp = Instant.now();

    public static <T> ApiResponse<T> success(T data, String message) {
        HttpStatus status = HttpStatus.OK;
        return ApiResponse.<T>builder()
            .status(status.value())
            .message(message)
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        HttpStatus status = HttpStatus.CREATED;
        return ApiResponse.<T>builder()
            .status(status.value())
            .message(message)
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> error(int status, T data, String message) {
        return ApiResponse.<T>builder()
            .status(status)
            .message(message)
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
            .status(status)
            .message(message)
            .build();
    }
}