package com.roomwise.professor.data.result;

public class AppError {
    public enum Type {
        VALIDATION,
        UNAUTHORIZED,
        NETWORK,
        SERVER,
        UNKNOWN
    }

    private final Type type;
    private final String userMessage;
    private final Throwable cause;

    public AppError(Type type, String userMessage, Throwable cause) {
        this.type = type;
        this.userMessage = userMessage;
        this.cause = cause;
    }

    public Type getType() {
        return type;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public Throwable getCause() {
        return cause;
    }
}
