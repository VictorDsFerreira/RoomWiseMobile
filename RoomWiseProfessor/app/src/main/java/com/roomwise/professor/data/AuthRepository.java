package com.roomwise.professor.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.roomwise.professor.data.remote.SupabaseRestClient;
import com.roomwise.professor.data.result.DataResult;
import com.roomwise.professor.data.result.ErrorMapper;
import com.roomwise.professor.data.session.ProfessorSession;

public class AuthRepository implements AuthDataSource {
    private final SupabaseRestClient api;
    private final Gson gson;

    public AuthRepository() {
        this(new SupabaseRestClient(), new Gson());
    }

    public AuthRepository(SupabaseRestClient api, Gson gson) {
        this.api = api;
        this.gson = gson;
    }

    @Override
    public DataResult<ProfessorSession> login(String usuario, String senha) {
        try {
            String body = "{\"p_usuario\":\"" + escapeJson(usuario.trim()) + "\",\"p_senha\":\"" + escapeJson(senha) + "\"}";
            String raw = api.postRpc("validar_login", body);
            JsonObject row = firstRow(raw);
            boolean senhaValida = readBoolean(row, "senha_valida", "senhaValida");
            if (!senhaValida) {
                throw new IllegalStateException("Usuario ou senha incorretos.");
            }

            String tipo = readString(row, "tipo");
            if (!"professor".equalsIgnoreCase(tipo)) {
                throw new IllegalStateException("Este aplicativo e apenas para professores.");
            }

            long usuarioId = readLong(row, "usuario_id", "usuarioId");
            String nome = readString(row, "nome");
            String usuarioRetorno = readString(row, "usuario");
            ProfessorSession session = new ProfessorSession(usuarioId, valueOrDash(nome), valueOrDash(usuarioRetorno));
            return DataResult.success(session);
        } catch (Exception e) {
            return DataResult.error(ErrorMapper.from(e, "Falha no login."));
        }
    }

    private static String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private static String escapeJson(String input) {
        if (input == null) return "";
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static JsonObject firstRow(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalStateException("Resposta vazia do servidor.");
        }
        JsonElement parsed = JsonParser.parseString(raw);
        if (parsed.isJsonArray()) {
            if (parsed.getAsJsonArray().size() == 0) {
                throw new IllegalStateException("Usuario ou senha incorretos.");
            }
            JsonElement first = parsed.getAsJsonArray().get(0);
            if (!first.isJsonObject()) {
                throw new IllegalStateException("Formato de resposta invalido.");
            }
            return first.getAsJsonObject();
        }
        if (parsed.isJsonObject()) {
            return parsed.getAsJsonObject();
        }
        throw new IllegalStateException("Formato de resposta invalido.");
    }

    private static String readString(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return "";
        try {
            return obj.get(key).getAsString();
        } catch (Exception ignored) {
            return "";
        }
    }

    private static long readLong(JsonObject obj, String... keys) {
        if (obj == null) return 0L;
        for (String key : keys) {
            if (!obj.has(key) || obj.get(key).isJsonNull()) continue;
            try {
                return obj.get(key).getAsLong();
            } catch (Exception ignored) {
                // Try next alias
            }
        }
        return 0L;
    }

    private static boolean readBoolean(JsonObject obj, String... keys) {
        if (obj == null) return false;
        for (String key : keys) {
            if (!obj.has(key) || obj.get(key).isJsonNull()) continue;
            JsonElement value = obj.get(key);
            try {
                if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
                    return value.getAsBoolean();
                }
                if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
                    return value.getAsInt() != 0;
                }
                String text = value.getAsString().trim().toLowerCase();
                if ("true".equals(text) || "t".equals(text) || "1".equals(text) || "sim".equals(text)) {
                    return true;
                }
                if ("false".equals(text) || "f".equals(text) || "0".equals(text) || "nao".equals(text) || "não".equals(text)) {
                    return false;
                }
            } catch (Exception ignored) {
                // Try next alias
            }
        }
        return false;
    }
}
