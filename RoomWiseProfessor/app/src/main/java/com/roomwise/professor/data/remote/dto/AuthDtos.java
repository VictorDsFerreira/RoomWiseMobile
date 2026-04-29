package com.roomwise.professor.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public final class AuthDtos {
    private AuthDtos() {}

    public static class ValidarLoginRpcRow {
        @SerializedName("usuario_id")
        public long usuarioId;
        public String nome;
        public String usuario;
        @SerializedName("cod_permissao")
        public int codPermissao;
        public String tipo;
        @SerializedName("senha_valida")
        public boolean senhaValida;
    }

    public static class TurmaIdRow {
        @SerializedName("cod_turma")
        public long codTurma;
    }
}
