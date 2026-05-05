package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExamCountdownActivity extends AppCompatActivity {
    private static final String TAG = "ExamCountdownActivity";

    // Bottom nav
    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;

    // Back button
    TextView btnBack;
    private Button btnAddExam;
    private LinearLayout examListContainer;
    private TextView tvNextExamInDays;
    private TextView tvNextExamSummary;
    private TextView tvTotalExams;
    private TextView tvThisMonthExams;
    private TextView tvAvgExamProgress;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_countdown);
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // 🔹 INIT NAVIGATION
        navDashboard = findViewById(R.id.navDashboard);
        navCalendar = findViewById(R.id.navCalendar);
        navTasks = findViewById(R.id.navTasks);
        navProgress = findViewById(R.id.navProgress);
        navProfile = findViewById(R.id.navProfile);

        // 🔹 BACK BUTTON
        btnBack = findViewById(R.id.btnBack);
        btnAddExam = findViewById(R.id.btnAddExam);
        examListContainer = findViewById(R.id.examListContainer);
        tvNextExamInDays = findViewById(R.id.tvNextExamInDays);
        tvNextExamSummary = findViewById(R.id.tvNextExamSummary);
        tvTotalExams = findViewById(R.id.tvTotalExams);
        tvThisMonthExams = findViewById(R.id.tvThisMonthExams);
        tvAvgExamProgress = findViewById(R.id.tvAvgExamProgress);

        // 🔥 NAVIGATION CLICK HANDLERS

        navDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        navCalendar.setOnClickListener(v ->
                startActivity(new Intent(this, CalendarActivity.class)));

        navTasks.setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        navProgress.setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        // 🔙 BACK
        btnBack.setOnClickListener(v -> finish());
        btnAddExam.setOnClickListener(v -> startActivity(new Intent(this, AddExamActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchExams();
    }

    private void fetchExams() {
        if (currentUser == null) {
            Log.e(TAG, "Current user null in fetchExams");
            return;
        }
        firestore.collection("users").document(currentUser.getUid()).collection("exams")
                .orderBy("examDateMillis")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    examListContainer.removeAllViews();
                    int total = queryDocumentSnapshots.size();
                    int thisMonth = 0;
                    int totalProgress = 0;
                    Long firstExamMillis = null;
                    String firstExamTitle = "No exams";
                    Calendar now = Calendar.getInstance();
                    int currentMonth = now.get(Calendar.MONTH);
                    int currentYear = now.get(Calendar.YEAR);
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String title = doc.getString("title");
                        Long examDateMillis = doc.getLong("examDateMillis");
                        Long progressLong = doc.getLong("progress");
                        int progress = progressLong == null ? 0 : progressLong.intValue();
                        totalProgress += progress;
                        if (examDateMillis != null) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(examDateMillis);
                            if (cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear) {
                                thisMonth++;
                            }
                            if (firstExamMillis == null) {
                                firstExamMillis = examDateMillis;
                                firstExamTitle = title == null ? "Upcoming Exam" : title;
                            }
                        }
                        addExamCard(title == null ? "Untitled Exam" : title, examDateMillis, progress);
                    }

                    tvTotalExams.setText(String.valueOf(total));
                    tvThisMonthExams.setText(String.valueOf(thisMonth));
                    tvAvgExamProgress.setText(total == 0 ? "0%" : ((totalProgress / total) + "%"));
                    if (firstExamMillis == null) {
                        tvNextExamInDays.setText("No Exams");
                        tvNextExamSummary.setText("Add an exam to get started");
                    } else {
                        long days = Math.max(0, (firstExamMillis - System.currentTimeMillis()) / (1000 * 60 * 60 * 24));
                        tvNextExamInDays.setText(days + " Days");
                        tvNextExamSummary.setText(firstExamTitle + "\n" + formatDate(firstExamMillis));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch exams", e));
    }

    private void addExamCard(String title, Long examDateMillis, int progress) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(30, 30, 30, 30);
        card.setBackgroundResource(R.drawable.card_purple);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 16, 0, 0);
        card.setLayoutParams(params);

        TextView titleTv = new TextView(this);
        titleTv.setText(title);
        titleTv.setTextSize(16f);
        titleTv.setTypeface(null, android.graphics.Typeface.BOLD);
        TextView dateTv = new TextView(this);
        dateTv.setText(examDateMillis == null ? "Date not set" : formatDate(examDateMillis));
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(Math.max(0, Math.min(progress, 100)));

        card.addView(titleTv);
        card.addView(dateTv);
        card.addView(progressBar);
        examListContainer.addView(card);
    }

    private String formatDate(long time) {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(time));
    }
}