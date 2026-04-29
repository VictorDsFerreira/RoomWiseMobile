package com.roomwise.professor.data.remote;

public class SupabaseHttpException extends Exception {
    public SupabaseHttpException(int code, String message) {
        super("HTTP " + code + ": " + message);
    }
}
