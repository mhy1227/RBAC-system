package com.czj.rbac.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(ResponseCode responseCode) {
        return new Result<>(responseCode.getCode(), responseCode.getMessage(), null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ResponseCode.ERROR.getCode(), message, null);
    }
} 