package com.roomwise.professor.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public final class AulaDtos {
    private AulaDtos() {}

    public static class CategoriaDto {
        public Long id;
        public String nome;
    }

    public static class DisciplinaDto {
        @SerializedName("cod_disciplina")
        public Long codDisciplina;
        @SerializedName("nome_discp")
        public String nomeDiscp;
        @SerializedName("categoria_id")
        public Long categoriaId;
        public CategoriaDto categoria;
    }

    public static class ProfessorDto {
        @SerializedName("cod_prof")
        public Long codProf;
        public String nome;
    }

    public static class TurmaDto {
        @SerializedName("cod_turma")
        public Long codTurma;
        @SerializedName("nome_turma")
        public String nomeTurma;
        public ProfessorDto professor;
        public DisciplinaDto disciplina;
    }

    public static class SalaDto {
        @SerializedName("cod_sala")
        public Long codSala;
        @SerializedName("nome_sala")
        public String nomeSala;
        public String local;
    }

    public static class AulaDto {
        @SerializedName("cod_aula")
        public long codAula;
        @SerializedName("cod_turma")
        public long codTurma;
        @SerializedName("cod_sala")
        public long codSala;
        @SerializedName("data_hora")
        public String dataHora;
        @SerializedName("data_hora_fim")
        public String dataHoraFim;
        @SerializedName("agrupamento_id")
        public String agrupamentoId;
        public TurmaDto turma;
        public SalaDto sala;
    }
}
