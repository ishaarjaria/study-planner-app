package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
<<<<<<< HEAD
import android.view.ViewGroup;
=======
>>>>>>> 58c259c (new changes)
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
<<<<<<< HEAD
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
=======
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
>>>>>>> 58c259c (new changes)
import java.util.Locale;

public class ExamCountdownActivity extends AppCompatActivity {
    private static final String TAG = "ExamCountdownActivity";

    // Bottom nav
    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;

    // Back button
    TextView btnBack;
    private Button btnAddExam;
    private LinearLayout examListContainer;
<<<<<<< HEAD
    private TextView tvNextExamInDays;
    private TextView tvNextExamSummary;
    private TextView tvTotalExams;
    private TextView tvThisMonthExams;
    private TextView tvAvgExamProgress;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
=======
    private TextView tvNextExamDays;
    private TextView tvNextExamDetail;
    private TextView tvTotalExams;
    private TextView tvThisMonthExams;
    private TextView tvAvgProgress;
    private FirebaseFirestore firestore;
    private String uid;
    private ListenerRegistration examsListener;
    private static final String TAG = "ExamCountdownActivity";
>>>>>>> 58c259c (new changes)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_countdown);
        firestore = FirebaseFirestore.getInstance();
<<<<<<< HEAD
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
=======
        uid = FirebaseAuth.getInstance().getCurrentUser() == null
                ? null : FirebaseAuth.getInstance().getCurrentUser().getUid();
>>>>>>> 58c259c (new changes)

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
<<<<<<< HEAD
        tvNextExamInDays = findViewById(R.id.tvNextExamInDays);
        tvNextExamSummary = findViewById(R.id.tvNextExamSummary);
        tvTotalExams = findViewById(R.id.tvTotalExams);
        tvThisMonthExams = findViewById(R.id.tvThisMonthExams);
        tvAvgExamProgress = findViewById(R.id.tvAvgExamProgress);
=======
        tvNextExamDays = findViewById(R.id.tvNextExamDays);
        tvNextExamDetail = findViewById(R.id.tvNextExamDetail);
        tvTotalExams = findViewById(R.id.tvTotalExams);
        tvThisMonthExams = findViewById(R.id.tvThisMonthExams);
        tvAvgProgress = findViewById(R.id.tvAvgProgress);

        btnAddExam.setOnClickListener(v -> startActivity(new Intent(this, AddExamActivity.class)));
>>>>>>> 58c259c (new changes)

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
<<<<<<< HEAD
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
=======

        if (uid == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (uid != null && examsListener == null) {
            observeExamsRealtime();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (examsListener != null) {
            examsListener.remove();
            examsListener = null;
        }
    }

    private void loadExams() {
        firestore.collection("exams")
                .whereEqualTo("uid", uid)
                .orderBy("dateMillis")
                .get()
                .addOnSuccessListener(query -> {
                    List<DocumentSnapshot> docs = query.getDocuments();
                    examListContainer.removeAllViews();
                    int thisMonth = 0;
                    int progressSum = 0;

                    Calendar now = Calendar.getInstance();
                    for (int i = 0; i < docs.size(); i++) {
                        DocumentSnapshot doc = docs.get(i);
                        String title = doc.getString("title");
                        Long dateMillis = doc.getLong("dateMillis");
                        Long progress = doc.getLong("progress");
                        int progressVal = progress == null ? 0 : progress.intValue();
                        progressSum += progressVal;
                        if (dateMillis != null) {
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(dateMillis);
                            if (c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                                    && c.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                                thisMonth++;
                            }
                        }
                        addExamCard(title == null ? "Untitled Exam" : title, dateMillis, progressVal);
                    }

                    tvTotalExams.setText(String.valueOf(docs.size()));
                    tvThisMonthExams.setText(String.valueOf(thisMonth));
                    int avg = docs.isEmpty() ? 0 : progressSum / docs.size();
                    tvAvgProgress.setText(avg + "%");

                    if (!docs.isEmpty()) {
                        DocumentSnapshot first = docs.get(0);
                        String firstTitle = first.getString("title");
                        Long firstDate = first.getLong("dateMillis");
                        tvNextExamDetail.setText((firstTitle == null ? "Upcoming Exam" : firstTitle)
                                + "\n" + formatDate(firstDate));
                        long days = firstDate == null ? 0 : Math.max(0L,
                                (firstDate - System.currentTimeMillis()) / (1000L * 60L * 60L * 24L));
                        tvNextExamDays.setText(days + " Days");
                    } else {
                        tvNextExamDetail.setText("No exams added yet");
                        tvNextExamDays.setText("0 Days");
>>>>>>> 58c259c (new changes)
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch exams", e));
    }

<<<<<<< HEAD
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
=======
    private void observeExamsRealtime() {
        examsListener = firestore.collection("exams")
                .whereEqualTo("uid", uid)
                .orderBy("dateMillis")
                .addSnapshotListener((query, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Realtime exam listener failed", e);
                        return;
                    }
                    if (query == null) {
                        Log.e(TAG, "Realtime exam query null");
                        return;
                    }
                    List<DocumentSnapshot> docs = query.getDocuments();
                    examListContainer.removeAllViews();
                    int thisMonth = 0;
                    int progressSum = 0;
                    Calendar now = Calendar.getInstance();
                    for (int i = 0; i < docs.size(); i++) {
                        DocumentSnapshot doc = docs.get(i);
                        String title = doc.getString("title");
                        Long dateMillis = doc.getLong("dateMillis");
                        Long progress = doc.getLong("progress");
                        int progressVal = progress == null ? 0 : progress.intValue();
                        progressSum += progressVal;
                        if (dateMillis != null) {
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(dateMillis);
                            if (c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                                    && c.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                                thisMonth++;
                            }
                        }
                        addExamCard(title == null ? "Untitled Exam" : title, dateMillis, progressVal);
                    }
                    tvTotalExams.setText(String.valueOf(docs.size()));
                    tvThisMonthExams.setText(String.valueOf(thisMonth));
                    int avg = docs.isEmpty() ? 0 : progressSum / docs.size();
                    tvAvgProgress.setText(avg + "%");
                    if (!docs.isEmpty()) {
                        DocumentSnapshot first = docs.get(0);
                        String firstTitle = first.getString("title");
                        Long firstDate = first.getLong("dateMillis");
                        tvNextExamDetail.setText((firstTitle == null ? "Upcoming Exam" : firstTitle)
                                + "\n" + formatDate(firstDate));
                        long days = firstDate == null ? 0 : Math.max(0L,
                                (firstDate - System.currentTimeMillis()) / (1000L * 60L * 60L * 24L));
                        tvNextExamDays.setText(days + " Days");
                    } else {
                        tvNextExamDetail.setText("No exams added yet");
                        tvNextExamDays.setText("0 Days");
                    }
                    Log.d(TAG, "Exam UI updated count=" + docs.size());
                });
    }

    private void addExamCard(String title, Long dateMillis, int progress) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(14, 14, 14, 14);
        card.setBackgroundResource(R.drawable.card_purple);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 0);
        card.setLayoutParams(params);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(16f);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView dateView = new TextView(this);
        dateView.setText(formatDate(dateMillis));
        dateView.setTextSize(12f);

        TextView progressView = new TextView(this);
        progressView.setText("Preparation Progress: " + progress + "%");
        progressView.setTextSize(12f);

        card.addView(titleView);
        card.addView(dateView);
        card.addView(progressView);
        examListContainer.addView(card);
    }

    private String formatDate(Long millis) {
        if (millis == null) return "Date not set";
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(millis);
>>>>>>> 58c259c (new changes)
    }
}