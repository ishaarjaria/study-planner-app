package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class ExamCountdownActivity extends AppCompatActivity {

    private static final String TAG = "ExamCountdownActivity";

    private LinearLayout examListContainer;
    private TextView tvNextExamDays, tvNextExamDetail, tvTotalExams, tvThisMonthExams, tvAvgProgress;
    private TextView btnBack;

    private FirebaseFirestore firestore;
    private String uid;
    private ListenerRegistration listener;
    private final Random random = new Random();
    private final int[] cardDrawables = {
            R.drawable.card_green,
            R.drawable.card_blue,
            R.drawable.card_yellow,
            R.drawable.card_purple,
            R.drawable.card_pink,
            R.drawable.card_red
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_countdown);

        firestore = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        examListContainer = findViewById(R.id.examListContainer);
        tvNextExamDays = findViewById(R.id.tvNextExamDays);
        tvNextExamDetail = findViewById(R.id.tvNextExamDetail);
        tvTotalExams = findViewById(R.id.tvTotalExams);
        tvThisMonthExams = findViewById(R.id.tvThisMonthExams);
        tvAvgProgress = findViewById(R.id.tvAvgProgress);
        btnBack = findViewById(R.id.btnBack);

        Button btnAddExam = findViewById(R.id.btnAddExam);
        btnAddExam.setOnClickListener(v ->
                startActivity(new Intent(this, AddExamActivity.class)));
        btnBack.setOnClickListener(v -> navigateToLanding());

        // Navigation
        findViewById(R.id.navDashboard).setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        findViewById(R.id.navCalendar).setOnClickListener(v ->
                startActivity(new Intent(this, CalendarActivity.class)));

        findViewById(R.id.navTasks).setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        findViewById(R.id.navProgress).setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));

        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        observeExams();
    }

    private void observeExams() {
        listener = firestore.collection("users")
                .document(uid)
                .collection("exams")
                .orderBy("dateMillis")
                .addSnapshotListener((query, e) -> {

                    if (e != null || query == null) {
                        Log.e(TAG, "Error loading exams", e);
                        return;
                    }

                    examListContainer.removeAllViews();

                    int total = query.size();
                    int thisMonth = 0;
                    int progressSum = 0;

                    Calendar now = Calendar.getInstance();

                    Long firstDate = null;
                    String firstTitle = "No exams";

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        String title = doc.getString("title");
                        Long date = doc.getLong("dateMillis");
                        Long progress = doc.getLong("progress");

                        int prog;
                        if (progress != null) {
                            prog = progress.intValue();
                        } else if (date != null) {
                            long daysLeft = Math.max(0, (date - System.currentTimeMillis()) / (1000 * 60 * 60 * 24));
                            prog = (int) Math.max(0, 100 - Math.min(100, daysLeft * 5));
                        } else {
                            prog = 0;
                        }
                        progressSum += prog;

                        if (date != null) {
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(date);

                            if (c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                                    && c.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                                thisMonth++;
                            }

                            if (firstDate == null) {
                                firstDate = date;
                                firstTitle = title == null ? "Upcoming Exam" : title;
                            }
                        }

                        addExamCard(
                                title == null ? "Untitled Exam" : title,
                                date,
                                prog
                        );
                    }

                    tvTotalExams.setText(String.valueOf(total));
                    tvThisMonthExams.setText(String.valueOf(thisMonth));

                    int avg = total == 0 ? 0 : progressSum / total;
                    tvAvgProgress.setText(avg + "%");

                    if (firstDate == null) {
                        tvNextExamDetail.setText("No exams added");
                        tvNextExamDays.setText("0 Days");
                    } else {
                        long days = Math.max(0,
                                (firstDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24));

                        tvNextExamDetail.setText(firstTitle + "\n" + formatDate(firstDate));
                        tvNextExamDays.setText(days + " Days");
                    }
                });
    }

    private void addExamCard(String title, Long date, int progress) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        int cardPadding = dpToPx(16);
        card.setPadding(cardPadding, cardPadding, cardPadding, cardPadding);
        card.setBackgroundResource(cardDrawables[random.nextInt(cardDrawables.length)]);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(24), dpToPx(14), dpToPx(24), 0);
        card.setLayoutParams(params);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextSize(15f);

        TextView tvDate = new TextView(this);
        tvDate.setText(formatDate(date));
        tvDate.setTextSize(13f);

        TextView tvProgress = new TextView(this);
        tvProgress.setText("Progress: " + progress + "%");
        tvProgress.setPadding(0, 6, 0, 6);

        ProgressBar progressBar = new ProgressBar(this, null,
                android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(Math.max(0, Math.min(100, progress)));
        progressBar.setProgressDrawable(getDrawable(R.drawable.progress_bar));

        card.addView(tvTitle);
        card.addView(tvDate);
        card.addView(tvProgress);
        card.addView(progressBar);

        examListContainer.addView(card);
    }

    private void navigateToLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private String formatDate(Long millis) {
        if (millis == null) return "No date";
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(millis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }
}