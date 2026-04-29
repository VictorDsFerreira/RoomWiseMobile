package com.roomwise.professor.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.roomwise.professor.data.AuthDataSource;
import com.roomwise.professor.data.AuthRepository;
import com.roomwise.professor.data.result.DataResult;
import com.roomwise.professor.data.session.ProfessorSession;
import com.roomwise.professor.data.session.SessionRepository;
import com.roomwise.professor.databinding.ActivityLoginBinding;
import com.roomwise.professor.ui.grade.GradeActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AuthDataSource authRepository = new AuthRepository();
    private SessionRepository sessionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionRepository = new SessionRepository(getApplicationContext());

        binding.btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String usuario = binding.etUsuario.getText().toString().trim();
        String senha = binding.etSenha.getText().toString();
        if (usuario.isEmpty() || senha.isEmpty()) {
            render(LoginUiState.error("Preencha usuario e senha."));
            return;
        }

        render(LoginUiState.loading());
        executor.execute(() -> {
            DataResult<ProfessorSession> result = authRepository.login(usuario, senha);
            runOnUiThread(() -> {
                if (result.isSuccess()) {
                    ProfessorSession session = result.getData();
                    if (session != null) {
                        sessionRepository.save(session);
                    }
                    render(LoginUiState.idle());
                    Intent intent = new Intent(LoginActivity.this, GradeActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                String message = (result.getError() != null) ? result.getError().getUserMessage() : "Falha no login.";
                render(LoginUiState.error(message));
            });
        });
    }

    private void render(LoginUiState state) {
        boolean loading = state.getStatus() == LoginUiState.Status.LOADING;
        binding.progressLogin.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
        binding.etUsuario.setEnabled(!loading);
        binding.etSenha.setEnabled(!loading);
        if (state.getStatus() == LoginUiState.Status.ERROR) {
            binding.tvError.setText(state.getMessage() == null ? "Falha no login." : state.getMessage());
            binding.tvError.setVisibility(View.VISIBLE);
        } else {
            binding.tvError.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
