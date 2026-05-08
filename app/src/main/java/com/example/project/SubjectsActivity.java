package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Random;

public class SubjectsActivity extends AppCompatActivity {
    private static final String TAG = "SubjectsActivity";

    Button btnAddSubject;
    LinearLayout subjectsContainer;
    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    TextView btnBack, tvSubjectsCount, tvAvgProgress, tvTotalHours;
    private ListenerRegistration subjectListener;
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
        setContentView(R.layout.activity_subjects);

        btnAddSubject = findViewById(R.id.btnAddSubject);
        subjectsContainer = findViewById(R.id.subjectsContainer);
        navDashboard = findViewById(R.id.navDashboard);
        navCalendar = findViewById(R.id.navCalendar);
        navTasks = findViewById(R.id.navTasks);
        navProgress = findViewById(R.id.navProgress);
        navProfile = findViewById(R.id.navProfile);
        btnBack = findViewById(R.id.btnBack);
        tvSubjectsCount = findViewById(R.id.tvSubjectsCount);
        tvAvgProgress = findViewById(R.id.tvAvgProgress);
        tvTotalHours = findViewById(R.id.tvTotalHours);

        // ➕ Open Add Subject
        btnAddSubject.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddSubjectActivity.class);
            startActivityForResult(intent, 1);
        });

        btnBack.setOnClickListener(v -> navigateToLanding());
        navDashboard.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        navCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        navTasks.setOnClickListener(v -> startActivity(new Intent(this, TasksActivity.class)));
        navProgress.setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        observeSubjects();
    }

    private void addSubject(String name, String level, String progress, String hours) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        card.setBackgroundResource(cardDrawables[random.nextInt(cardDrawables.length)]);
        card.setTag("dynamic_subject");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(24), 0, dpToPx(24), dpToPx(12));
        card.setLayoutParams(params);

        TextView tvName = new TextView(this);
        tvName.setText(name == null ? "Subject" : name);
        tvName.setTextSize(15);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvLevel = new TextView(this);
        tvLevel.setText(level == null ? "" : level);
        tvLevel.setTextSize(13f);
        tvLevel.setPadding(0, dpToPx(4), 0, 0);

        TextView tvProgress = new TextView(this);
        tvProgress.setText("Progress: " + (progress == null ? "0" : progress) + "%");
        tvProgress.setTextSize(13f);
        tvProgress.setPadding(0, dpToPx(4), 0, 0);

        TextView tvHours = new TextView(this);
        tvHours.setText("Study Time: " + (hours == null ? "0" : hours) + "h");
        tvHours.setTextSize(13f);
        tvHours.setPadding(0, dpToPx(4), 0, 0);

        card.addView(tvName);
        card.addView(tvLevel);
        card.addView(tvProgress);
        card.addView(tvHours);

        subjectsContainer.addView(card);
    }

    private void observeSubjects() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        subjectListener = FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .collection("subjects")
                .addSnapshotListener((query, e) -> {
                    if (e != null || query == null) {
                        Log.e(TAG, "Subject load failed", e);
                        return;
                    }
                    subjectsContainer.removeAllViews();
                    int totalSubjects = 0;
                    int progressSum = 0;
                    int totalHours = 0;
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        totalSubjects++;
                        int p = parseIntValue(doc.getString("progress"));
                        int h = parseIntValue(doc.getString("hours"));
                        progressSum += p;
                        totalHours += h;
                        addSubject(
                                doc.getString("name"),
                                doc.getString("level"),
                                doc.getString("progress"),
                                doc.getString("hours")
                        );
                    }
                    int avg = totalSubjects == 0 ? 0 : progressSum / totalSubjects;
                    tvSubjectsCount.setText(totalSubjects + "\nSubjects");
                    tvAvgProgress.setText(avg + "%\nAvg Progress");
                    tvTotalHours.setText(totalHours + "h\nTotal Hours");
                });
    }

    private void navigateToLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private int parseIntValue(String raw) {
        if (raw == null || raw.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(raw.trim());
        } catch (Exception ignored) {
            return 0;
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subjectListener != null) {
            subjectListener.remove();
        }
    }
}