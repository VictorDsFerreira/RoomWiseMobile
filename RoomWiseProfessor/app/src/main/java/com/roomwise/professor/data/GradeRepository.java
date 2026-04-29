package com.roomwise.professor.data;

import com.google.gson.Gson;
import com.roomwise.professor.data.remote.SupabaseRestClient;
import com.roomwise.professor.data.remote.dto.AulaDtos;
import com.roomwise.professor.data.remote.dto.AuthDtos;
import com.roomwise.professor.data.result.DataResult;
import com.roomwise.professor.data.result.ErrorMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GradeRepository implements GradeDataSource {
    private final SupabaseRestClient api;
    private final Gson gson;

    public GradeRepository() {
        this(new SupabaseRestClient(), new Gson());
    }

    public GradeRepository(SupabaseRestClient api, Gson gson) {
        this.api = api;
        this.gson = gson;
    }

    @Override
    public DataResult<List<AulaDtos.AulaDto>> fetchAulasDoProfessor(long codProf) {
        try {
            String turmasPath = "/turma?select=cod_turma&cod_prof=eq." + codProf;
            String turmasRaw = api.getRest(turmasPath);
            AuthDtos.TurmaIdRow[] turmas = gson.fromJson(turmasRaw, AuthDtos.TurmaIdRow[].class);
            if (turmas == null || turmas.length == 0) {
                return DataResult.success(Collections.emptyList());
            }

            List<Long> ids = Arrays.stream(turmas).map(t -> t.codTurma).collect(Collectors.toList());
            String inFilter = "in.(" + ids.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
            String select = "*,turma(*,professor(*),disciplina(*,categoria(*))),sala(*)";

            String aulasPath = "/aula?select=" + encode(select)
                    + "&cod_turma=" + encode(inFilter)
                    + "&order=" + encode("data_hora.asc");

            String aulasRaw = api.getRest(aulasPath);
            AulaDtos.AulaDto[] aulas = gson.fromJson(aulasRaw, AulaDtos.AulaDto[].class);
            if (aulas == null) return DataResult.success(Collections.emptyList());
            return DataResult.success(Arrays.asList(aulas));
        } catch (Exception e) {
            return DataResult.error(ErrorMapper.from(e, "Erro ao carregar aulas."));
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
