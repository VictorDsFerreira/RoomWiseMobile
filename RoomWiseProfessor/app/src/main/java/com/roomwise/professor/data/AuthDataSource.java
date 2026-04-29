package com.roomwise.professor.data;

import com.roomwise.professor.data.result.DataResult;
import com.roomwise.professor.data.session.ProfessorSession;

public interface AuthDataSource {
    DataResult<ProfessorSession> login(String usuario, String senha);
}
