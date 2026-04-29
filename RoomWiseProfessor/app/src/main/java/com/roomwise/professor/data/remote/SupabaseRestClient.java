package com.roomwise.professor.data.remote;

import com.roomwise.professor.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseRestClient {
    private static final MediaType JSON_MEDIA = MediaType.parse("application/json; charset=utf-8");

    private final String baseUrl;
    private final String anonKey;
    private final OkHttpClient httpClient;

    public SupabaseRestClient() {
        this(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY);
    }

    public SupabaseRestClient(String baseUrl, String anonKey) {
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.anonKey = anonKey == null ? "" : anonKey;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .build();
    }

    public String postRpc(String functionName, String jsonBody) throws Exception {
        assertConfigured();
        String url = baseUrl + "/rest/v1/rpc/" + functionName;
        RequestBody body = RequestBody.create(jsonBody, JSON_MEDIA);
        Request req = new Request.Builder()
                .url(url)
                .header("apikey", anonKey)
                .header("Authorization", "Bearer " + anonKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(body)
                .build();
        return execute(req);
    }

    public String getRest(String pathAndQuery) throws Exception {
        assertConfigured();
        String path = pathAndQuery.startsWith("/") ? pathAndQuery : "/" + pathAndQuery;
        String url = baseUrl + "/rest/v1" + path;
        Request req = new Request.Builder()
                .url(url)
                .header("apikey", anonKey)
                .header("Authorization", "Bearer " + anonKey)
                .header("Accept", "application/json")
                .get()
                .build();
        return execute(req);
    }

    private String execute(Request req) throws Exception {
        try (Response response = httpClient.newCall(req).execute()) {
            String text = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                String message = text.isEmpty() ? response.message() : text;
                throw new SupabaseHttpException(response.code(), message);
            }
            return text;
        } catch (IOException e) {
            throw new Exception("Falha de rede: " + e.getMessage(), e);
        }
    }

    private void assertConfigured() {
        if (baseUrl.isEmpty() || anonKey.isEmpty()) {
            throw new IllegalStateException("Defina SUPABASE_URL e SUPABASE_ANON_KEY em local.properties.");
        }
    }

    private static String trimTrailingSlash(String value) {
        if (value == null) return "";
        String out = value.trim();
        while (out.endsWith("/")) {
            out = out.substring(0, out.length() - 1);
        }
        return out;
    }
}
