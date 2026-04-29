package com.roomwise.professor.ui.grade;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.roomwise.professor.R;
import com.roomwise.professor.data.GradeDataSource;
import com.roomwise.professor.data.GradeRepository;
import com.roomwise.professor.data.remote.dto.AulaDtos;
import com.roomwise.professor.data.result.DataResult;
import com.roomwise.professor.data.session.ProfessorSession;
import com.roomwise.professor.data.session.SessionRepository;
import com.roomwise.professor.databinding.ActivityGradeBinding;
import com.roomwise.professor.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GradeActivity extends AppCompatActivity {
    private ActivityGradeBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final GradeDataSource gradeRepository = new GradeRepository();
    private SessionRepository sessionRepository;
    private final GradeDayAdapter adapter = new GradeDayAdapter();
    private ProfessorSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGradeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        sessionRepository = new SessionRepository(getApplicationContext());
        session = sessionRepository.getSession();
        if (session == null) {
            goToLogin();
            return;
        }

        binding.tvProfessorName.setText(session.getNome());
        binding.recyclerGrade.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerGrade.setAdapter(adapter);
        binding.btnRetry.setOnClickListener(v -> loadGrade());
        binding.swipeRefresh.setOnRefreshListener(this::loadGrade);
        loadGrade();
    }

    private void loadGrade() {
        render(GradeUiState.loading());
        executor.execute(() -> {
            DataResult<List<AulaDtos.AulaDto>> result = gradeRepository.fetchAulasDoProfessor(session.getCodProf());
            runOnUiThread(() -> {
                if (result.isSuccess()) {
                    List<AulaDtos.AulaDto> aulas = result.getData();
                    List<GradeDayUi> grouped = GradeUiMapper.map(aulas == null ? new ArrayList<>() : aulas);
                    render(GradeUiState.success(grouped));
                    return;
                }
                String message = (result.getError() != null) ? result.getError().getUserMessage() : "Erro ao carregar aulas.";
                render(GradeUiState.error(message));
            });
        });
    }

    private void render(GradeUiState state) {
        binding.swipeRefresh.setRefreshing(false);
        if (state.getStatus() == GradeUiState.Status.LOADING) {
            binding.progressContainer.setVisibility(View.VISIBLE);
            binding.contentContainer.setVisibility(View.GONE);
            binding.errorContainer.setVisibility(View.GONE);
            return;
        }
        if (state.getStatus() == GradeUiState.Status.ERROR) {
            binding.progressContainer.setVisibility(View.GONE);
            binding.contentContainer.setVisibility(View.GONE);
            binding.errorContainer.setVisibility(View.VISIBLE);
            binding.tvErrorMessage.setText(state.getMessage() == null ? "Erro ao carregar aulas." : state.getMessage());
            return;
        }
        binding.progressContainer.setVisibility(View.GONE);
        binding.errorContainer.setVisibility(View.GONE);
        binding.contentContainer.setVisibility(View.VISIBLE);
        adapter.submitList(state.getItems() == null ? new ArrayList<>() : state.getItems());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.grade_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            sessionRepository.clear();
            goToLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
