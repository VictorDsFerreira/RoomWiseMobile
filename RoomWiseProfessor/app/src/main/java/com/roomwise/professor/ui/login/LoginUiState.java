package com.roomwise.professor.ui.login;

public class LoginUiState {
    public enum Status {
        IDLE,
        LOADING,
        ERROR
    }

    private final Status status;
    private final String message;

    private LoginUiState(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public static LoginUiState idle() {
        return new LoginUiState(Status.IDLE, null);
    }

    public static LoginUiState loading() {
        return new LoginUiState(Status.LOADING, null);
    }

    public static LoginUiState error(String message) {
        return new LoginUiState(Status.ERROR, message);
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
