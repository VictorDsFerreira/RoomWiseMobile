package com.roomwise.professor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.roomwise.professor.data.session.ProfessorSession;
import com.roomwise.professor.data.session.SessionRepository;
import com.roomwise.professor.ui.grade.GradeActivity;
import com.roomwise.professor.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionRepository sessionRepository = new SessionRepository(getApplicationContext());
        ProfessorSession session = sessionRepository.getSession();
        Intent intent = (session == null)
                ? new Intent(this, LoginActivity.class)
                : new Intent(this, GradeActivity.class);
        startActivity(intent);
        finish();
    }
}
