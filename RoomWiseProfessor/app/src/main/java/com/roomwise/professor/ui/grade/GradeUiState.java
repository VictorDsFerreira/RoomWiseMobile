package com.roomwise.professor.ui.grade;

import java.util.List;

public class GradeUiState {
    public enum Status {
        LOADING,
        SUCCESS,
        ERROR
    }

    private final Status status;
    private final List<GradeDayUi> items;
    private final String message;

    private GradeUiState(Status status, List<GradeDayUi> items, String message) {
        this.status = status;
        this.items = items;
        this.message = message;
    }

    public static GradeUiState loading() {
        return new GradeUiState(Status.LOADING, null, null);
    }

    public static GradeUiState success(List<GradeDayUi> items) {
        return new GradeUiState(Status.SUCCESS, items, null);
    }

    public static GradeUiState error(String message) {
        return new GradeUiState(Status.ERROR, null, message);
    }

    public Status getStatus() {
        return status;
    }

    public List<GradeDayUi> getItems() {
        return items;
    }

    public String getMessage() {
        return message;
    }
}
