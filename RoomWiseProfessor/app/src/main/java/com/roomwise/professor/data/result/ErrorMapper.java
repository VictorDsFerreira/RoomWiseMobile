package com.roomwise.professor.data.result;

import com.roomwise.professor.data.remote.SupabaseHttpException;

import java.io.IOException;

public final class ErrorMapper {
    private ErrorMapper() {}

    public static AppError from(Throwable throwable, String fallbackMessage) {
        if (throwable instanceof IllegalArgumentException || throwable instanceof IllegalStateException) {
            String message = safeMessage(throwable, fallbackMessage);
            if (containsAuthHint(message)) {
                return new AppError(AppError.Type.UNAUTHORIZED, message, throwable);
            }
            return new AppError(AppError.Type.VALIDATION, message, throwable);
        }

        if (throwable instanceof SupabaseHttpException) {
            String msg = safeMessage(throwable, fallbackMessage);
            if (msg.contains("HTTP 401") || msg.contains("HTTP 403")) {
                return new AppError(AppError.Type.UNAUTHORIZED, "Sessao expirada. Faca login novamente.", throwable);
            }
            if (msg.contains("PGRST202") || msg.contains("validar_login")) {
                return new AppError(
                        AppError.Type.SERVER,
                        "RPC validar_login indisponivel no Supabase. Verifique nome/parametros da funcao e permissao EXECUTE para anon.",
                        throwable
                );
            }
            if (msg.contains("42P01") || msg.toLowerCase().contains("missing from-clause")) {
                return new AppError(
                        AppError.Type.SERVER,
                        "Erro SQL na funcao validar_login no Supabase (alias/tabela invalido). Corrija a query da funcao.",
                        throwable
                );
            }
            return new AppError(AppError.Type.SERVER, fallbackMessage, throwable);
        }

        if (throwable instanceof IOException || throwable.getCause() instanceof IOException) {
            return new AppError(AppError.Type.NETWORK, "Falha de rede. Verifique sua conexao.", throwable);
        }

        return new AppError(AppError.Type.UNKNOWN, fallbackMessage, throwable);
    }

    private static String safeMessage(Throwable throwable, String fallback) {
        String msg = throwable.getMessage();
        return (msg == null || msg.trim().isEmpty()) ? fallback : msg;
    }

    private static boolean containsAuthHint(String message) {
        String normalized = message == null ? "" : message.toLowerCase();
        return normalized.contains("senha") || normalized.contains("login") || normalized.contains("usuario");
    }
}
