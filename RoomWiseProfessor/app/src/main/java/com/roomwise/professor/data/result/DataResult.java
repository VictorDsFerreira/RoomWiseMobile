package com.roomwise.professor.data.result;

public class DataResult<T> {
    private final T data;
    private final AppError error;

    private DataResult(T data, AppError error) {
        this.data = data;
        this.error = error;
    }

    public static <T> DataResult<T> success(T data) {
        return new DataResult<>(data, null);
    }

    public static <T> DataResult<T> error(AppError error) {
        return new DataResult<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public T getData() {
        return data;
    }

    public AppError getError() {
        return error;
    }
}
