package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    private TextView tvHello, tvProgressPercent, tvProgressMeta, tvNextExamTitle, tvNextExamDays;
    private ProgressBar pbTodayProgress;

    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private ListenerRegistration taskListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Views
        tvHello = findViewById(R.id.tvHello);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        tvProgressMeta = findViewById(R.id.tvProgressMeta);
        tvNextExamTitle = findViewById(R.id.tvNextExamTitle);
        tvNextExamDays = findViewById(R.id.tvNextExamDays);
        pbTodayProgress = findViewById(R.id.pbTodayProgress);

        setupBottomNavigation();

        // Quick buttons
        findViewById(R.id.btnAddTask).setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        findViewById(R.id.btnTimer).setOnClickListener(v ->
                startActivity(new Intent(this, StudyTimerActivity.class)));

        findViewById(R.id.btnTimetable).setOnClickListener(v ->
                startActivity(new Intent(this, TimetableActivity.class)));

        loadUser();
        observeTaskProgress();
        loadNextExam();
    }

    private void setupBottomNavigation() {
        setNavClick(R.id.navDashboard, DashboardActivity.class, true);
        setNavClick(R.id.navCalendar, CalendarActivity.class, false);
        setNavClick(R.id.navTasks, TasksActivity.class, false);
        setNavClick(R.id.navProgress, ProgressActivity.class, false);
        setNavClick(R.id.navProfile, ProfileActivity.class, false);

        TextView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setClickable(true);
            btnBack.setOnClickListener(v -> navigateToLanding());
        }
    }

    private void setNavClick(int viewId, Class<?> target, boolean isCurrentPage) {
        android.view.View nav = findViewById(viewId);
        if (nav == null) {
            Log.e(TAG, "Missing nav view id: " + viewId);
            return;
        }
        nav.setClickable(true);
        nav.setFocusable(true);
        nav.setOnClickListener(v -> {
            if (!isCurrentPage) {
                startActivity(new Intent(this, target));
            }
        });
    }

    private void navigateToLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loadUser() {
        firestore.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    String name = doc.getString("name");
                    if (name == null || name.isEmpty()) name = "Student";
                    tvHello.setText("Hello, " + name + "!");
                })
                .addOnFailureListener(e -> Log.e(TAG, "User load failed", e));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser != null) {
            loadUser();
        }
    }

    private void observeTaskProgress() {
        taskListener = firestore.collection("users")
                .document(currentUser.getUid())
                .collection("tasks")
                .addSnapshotListener((query, e) -> {

                    if (e != null || query == null) {
                        Log.e(TAG, "Task listener error", e);
                        return;
                    }

                    int total = query.size();
                    int done = 0;

                    for (int i = 0; i < query.size(); i++) {
                        Boolean completed = query.getDocuments().get(i).getBoolean("completed");
                        if (Boolean.TRUE.equals(completed)) done++;
                    }

                    int percent = total == 0 ? 0 : (done * 100 / total);

                    pbTodayProgress.setProgress(percent);
                    tvProgressPercent.setText(percent + "%");
                    tvProgressMeta.setText(done + " of " + total + " tasks completed");
                });
    }

    private void loadNextExam() {
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("exams")
                .orderBy("dateMillis")
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {
                        tvNextExamTitle.setText("No upcoming exam");
                        tvNextExamDays.setText("Add an exam");
                        return;
                    }

                    String title = query.getDocuments().get(0).getString("title");
                    Long dateMillis = query.getDocuments().get(0).getLong("dateMillis");

                    tvNextExamTitle.setText(title != null ? title : "Exam");

                    if (dateMillis != null) {
                        long days = Math.max(0,
                                (dateMillis - System.currentTimeMillis()) / (1000 * 60 * 60 * 24));
                        tvNextExamDays.setText(days + " days remaining");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Exam load failed", e));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (taskListener != null) taskListener.remove();
    }
}