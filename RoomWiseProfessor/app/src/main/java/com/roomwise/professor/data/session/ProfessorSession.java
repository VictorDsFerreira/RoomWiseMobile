package com.roomwise.professor.data.session;

public class ProfessorSession {
    private final long codProf;
    private final String nome;
    private final String usuario;

    public ProfessorSession(long codProf, String nome, String usuario) {
        this.codProf = codProf;
        this.nome = nome;
        this.usuario = usuario;
    }

    public long getCodProf() {
        return codProf;
    }

    public String getNome() {
        return nome;
    }

    public String getUsuario() {
        return usuario;
    }
}
