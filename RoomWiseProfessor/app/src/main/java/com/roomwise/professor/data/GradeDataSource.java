package com.roomwise.professor.data;

import com.roomwise.professor.data.remote.dto.AulaDtos;
import com.roomwise.professor.data.result.DataResult;

import java.util.List;

public interface GradeDataSource {
    DataResult<List<AulaDtos.AulaDto>> fetchAulasDoProfessor(long codProf);
}
