package com.roomwise.professor.data.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionRepository {
    private static final String PREFS = "roomwise_professor_session";
    private static final String KEY_COD_PROF = "cod_prof";
    private static final String KEY_NOME = "nome";
    private static final String KEY_USUARIO = "usuario";

    private final SharedPreferences prefs;

    public SessionRepository(Context context) {
        this.prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public ProfessorSession getSession() {
        if (!prefs.contains(KEY_COD_PROF) || !prefs.contains(KEY_NOME) || !prefs.contains(KEY_USUARIO)) {
            return null;
        }
        long codProf = prefs.getLong(KEY_COD_PROF, -1L);
        String nome = prefs.getString(KEY_NOME, null);
        String usuario = prefs.getString(KEY_USUARIO, null);
        if (codProf < 0 || nome == null || usuario == null) {
            return null;
        }
        return new ProfessorSession(codProf, nome, usuario);
    }

    public void save(ProfessorSession session) {
        prefs.edit()
                .putLong(KEY_COD_PROF, session.getCodProf())
                .putString(KEY_NOME, session.getNome())
                .putString(KEY_USUARIO, session.getUsuario())
                .apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
