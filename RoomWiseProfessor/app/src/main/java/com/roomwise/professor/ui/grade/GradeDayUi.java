package com.roomwise.professor.ui.grade;

public class GradeDayUi {
    private final String titulo;
    private final String conteudo;

    public GradeDayUi(String titulo, String conteudo) {
        this.titulo = titulo;
        this.conteudo = conteudo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getConteudo() {
        return conteudo;
    }
}
