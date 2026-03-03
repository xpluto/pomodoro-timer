package com.pomodoro.common;

import lombok.Data;

@Data
public class Result<T> {

    private boolean success;
    private T data;
    private String message;

    private Result() {
    }

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> ok() {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}
