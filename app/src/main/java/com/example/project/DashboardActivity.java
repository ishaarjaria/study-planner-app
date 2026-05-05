package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
<<<<<<< HEAD
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
=======
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
>>>>>>> 58c259c (new changes)

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";

    TextView btnBack, navDashboard, navCalendar, navTasks, navProgress, navProfile;
<<<<<<< HEAD
    private TextView tvGreeting;
    private TextView tvDate;
    private TextView tvProgressPercent;
    private TextView tvProgressSummary;
    private TextView tvNextExamName;
    private TextView tvNextExamDays;
    private ProgressBar progressTasks;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;
=======
    private TextView tvHello;
    private TextView tvProgressPercent;
    private TextView tvProgressMeta;
    private TextView tvNextExamTitle;
    private TextView tvNextExamDays;
    private ProgressBar pbTodayProgress;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private ListenerRegistration taskProgressListener;
    private static final String TAG = "DashboardActivity";
>>>>>>> 58c259c (new changes)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
<<<<<<< HEAD

        tvGreeting = findViewById(R.id.tvGreeting);
        tvDate = findViewById(R.id.tvDate);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        tvProgressSummary = findViewById(R.id.tvProgressSummary);
        tvNextExamName = findViewById(R.id.tvNextExamName);
        tvNextExamDays = findViewById(R.id.tvNextExamDays);
        progressTasks = findViewById(R.id.progressTasks);
        applyDateAndGreeting();
=======
>>>>>>> 58c259c (new changes)

        // Back button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, LandingActivity.class));
            finish();
        });

        // Bottom navigation
        LinearLayout navDashboard = findViewById(R.id.navDashboard);
        LinearLayout navCalendar = findViewById(R.id.navCalendar);
        LinearLayout navTasks = findViewById(R.id.navTasks);
        LinearLayout navProgress = findViewById(R.id.navProgress);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        // Already on Dashboard → do nothing
        navDashboard.setOnClickListener(v -> { });

        navCalendar.setOnClickListener(v ->
                startActivity(new Intent(this, CalendarActivity.class)));

        navTasks.setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        navProgress.setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        LinearLayout btnAddTask = findViewById(R.id.btnAddTask);
        LinearLayout btnTimer = findViewById(R.id.btnTimer);
        LinearLayout btnTimetable = findViewById(R.id.btnTimetable);

        btnAddTask.setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        btnTimer.setOnClickListener(v ->
                startActivity(new Intent(this, StudyTimerActivity.class)));

        btnTimetable.setOnClickListener(v ->
                startActivity(new Intent(this, TimetableActivity.class)));

        tvHello = findViewById(R.id.tvHello);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        tvProgressMeta = findViewById(R.id.tvProgressMeta);
        tvNextExamTitle = findViewById(R.id.tvNextExamTitle);
        tvNextExamDays = findViewById(R.id.tvNextExamDays);
        pbTodayProgress = findViewById(R.id.pbTodayProgress);

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        loadUser();
        observeTaskProgress();
        loadNextExam();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser != null) {
            if (taskProgressListener == null) {
                observeTaskProgress();
            }
            loadNextExam();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (taskProgressListener != null) {
            taskProgressListener.remove();
            taskProgressListener = null;
        }
    }

    private void loadUser() {
        firestore.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(snapshot -> {
            String name = snapshot.getString("name");
            if (name == null || name.trim().isEmpty()) {
                name = "Student";
            }
            tvHello.setText("Hello, " + name + "!");
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to load user profile", e));
    }

    private void loadTaskProgress() {
        firestore.collection("tasks")
                .whereEqualTo("uid", currentUser.getUid())
                .get()
                .addOnSuccessListener(query -> {
                    int total = query.size();
                    int completed = 0;
                    for (int i = 0; i < query.size(); i++) {
                        Boolean done = query.getDocuments().get(i).getBoolean("completed");
                        if (Boolean.TRUE.equals(done)) {
                            completed++;
                        }
                    }
                    int percent = total == 0 ? 0 : (completed * 100 / total);
                    pbTodayProgress.setProgress(percent);
                    tvProgressPercent.setText(percent + "%");
                    tvProgressMeta.setText("↗ " + completed + " of " + total + " tasks completed");
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to load tasks", e));
    }

    private void observeTaskProgress() {
        taskProgressListener = firestore.collection("tasks")
                .whereEqualTo("uid", currentUser.getUid())
                .addSnapshotListener((query, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Task progress realtime listener failed", e);
                        return;
                    }
                    if (query == null) {
                        Log.e(TAG, "Task progress query is null");
                        return;
                    }
                    int total = query.size();
                    int completed = 0;
                    for (int i = 0; i < query.size(); i++) {
                        Boolean done = query.getDocuments().get(i).getBoolean("completed");
                        if (Boolean.TRUE.equals(done)) {
                            completed++;
                        }
                    }
                    int percent = total == 0 ? 0 : (completed * 100 / total);
                    pbTodayProgress.setProgress(percent);
                    tvProgressPercent.setText(percent + "%");
                    tvProgressMeta.setText("↗ " + completed + " of " + total + " tasks completed");
                    Log.d(TAG, "Dashboard progress updated completed=" + completed + ", total=" + total);
                });
    }

    private void loadNextExam() {
        firestore.collection("exams")
                .whereEqualTo("uid", currentUser.getUid())
                .orderBy("dateMillis")
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        tvNextExamTitle.setText("No upcoming exam");
                        tvNextExamDays.setText("Add an exam to start tracking");
                        return;
                    }
                    String title = query.getDocuments().get(0).getString("title");
                    Long dateMillis = query.getDocuments().get(0).getLong("dateMillis");
                    tvNextExamTitle.setText(title == null ? "Upcoming Exam" : title);
                    if (dateMillis == null) {
                        tvNextExamDays.setText("Date not set");
                        return;
                    }
                    long today = System.currentTimeMillis();
                    long diff = Math.max(0L, dateMillis - today);
                    long days = diff / (1000L * 60L * 60L * 24L);
                    tvNextExamDays.setText(days + " days remaining");
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to load exams", e));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadDashboardData();
    }

    private void loadDashboardData() {
        if (currentUser == null) {
            Log.e(TAG, "User not logged in");
            return;
        }
        String uid = currentUser.getUid();

        firestore.collection("users").document(uid).collection("tasks")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int total = queryDocumentSnapshots.size();
                    int done = 0;
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Boolean completed = doc.getBoolean("completed");
                        if (Boolean.TRUE.equals(completed)) {
                            done++;
                        }
                    }
                    int percent = total == 0 ? 0 : (done * 100 / total);
                    progressTasks.setProgress(percent);
                    tvProgressPercent.setText(percent + "%");
                    tvProgressSummary.setText(done + " of " + total + " tasks completed");
                })
                .addOnFailureListener(e -> Log.e(TAG, "Task fetch failed", e));

        firestore.collection("users").document(uid).collection("exams")
                .orderBy("examDateMillis", Query.Direction.ASCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        tvNextExamName.setText("No upcoming exams");
                        tvNextExamDays.setText("Add an exam to get started");
                        return;
                    }
                    DocumentSnapshot exam = queryDocumentSnapshots.getDocuments().get(0);
                    String name = exam.getString("title");
                    Long dateMillis = exam.getLong("examDateMillis");
                    tvNextExamName.setText(name == null ? "Upcoming Exam" : name);
                    if (dateMillis == null) {
                        tvNextExamDays.setText("Date not set");
                        return;
                    }
                    long diff = dateMillis - System.currentTimeMillis();
                    long days = Math.max(0, diff / (1000 * 60 * 60 * 24));
                    tvNextExamDays.setText(days + " days remaining");
                })
                .addOnFailureListener(e -> Log.e(TAG, "Exam fetch failed", e));
    }

    private void applyDateAndGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            tvGreeting.setText("Good morning, Student!");
        } else if (hour < 17) {
            tvGreeting.setText("Good afternoon, Student!");
        } else {
            tvGreeting.setText("Good evening, Student!");
        }
        tvDate.setText(android.text.format.DateFormat.format("EEEE, MMMM dd, yyyy", calendar));
    }
}