package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class ProgressActivity extends AppCompatActivity {
    private static final String TAG = "ProgressActivity";

    private FirebaseFirestore firestore;
    private String uid;

    private TextView tvThisWeekHours;
    private TextView tvLastWeekHours;
    private LinearLayout weeklyGraphContainer;
    private LinearLayout monthlyGraphContainer;
    private LinearLayout subjectProgressContainer;

    private final Map<String, Integer> subjectBaseProgressMap = new HashMap<>();
    private final Map<String, SubjectTaskStats> subjectTaskStatsMap = new HashMap<>();
    private final Map<String, SubjectExamStats> subjectExamStatsMap = new HashMap<>();
    private final List<String> orderedSubjectNames = new ArrayList<>();
    private final List<ListenerRegistration> listeners = new ArrayList<>();
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
        setContentView(R.layout.activity_progress);

        firestore = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        tvThisWeekHours = findViewById(R.id.tvThisWeekHours);
        tvLastWeekHours = findViewById(R.id.tvLastWeekHours);
        weeklyGraphContainer = findViewById(R.id.weeklyGraphContainer);
        monthlyGraphContainer = findViewById(R.id.monthlyGraphContainer);
        subjectProgressContainer = findViewById(R.id.subjectProgressContainer);

        setupBottomNavigation();
        observeProgressData();
    }

    private void setupBottomNavigation() {
        setNavClick(R.id.navDashboard, DashboardActivity.class, false);
        setNavClick(R.id.navCalendar, CalendarActivity.class, false);
        setNavClick(R.id.navTasks, TasksActivity.class, false);
        setNavClick(R.id.navProgress, ProgressActivity.class, true);
        setNavClick(R.id.navProfile, ProfileActivity.class, false);

        TextView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setClickable(true);
            btnBack.setOnClickListener(v -> navigateToLanding());
        } else {
            Log.e(TAG, "btnBack missing in activity_progress.xml");
        }
    }

    private void setNavClick(int viewId, Class<?> target, boolean isCurrentPage) {
        View nav = findViewById(viewId);
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

    private void observeProgressData() {
        listeners.add(firestore.collection("users").document(uid).collection("subjects")
                .addSnapshotListener((query, e) -> {
                    if (e != null || query == null) {
                        Log.e(TAG, "Subjects progress fetch failed", e);
                        return;
                    }
                    Map<String, Integer> local = new HashMap<>();
                    List<String> names = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String name = doc.getString("name");
                        if (name == null || name.trim().isEmpty()) continue;
                        names.add(name.trim());
                        Integer p = parseProgress(doc.get("progress"));
                        local.put(name.trim(), p);
                    }
                    synchronized (subjectBaseProgressMap) {
                        subjectBaseProgressMap.clear();
                        subjectBaseProgressMap.putAll(local);
                        orderedSubjectNames.clear();
                        orderedSubjectNames.addAll(names);
                    }
                    renderProgressUi();
                }));

        listeners.add(firestore.collection("users").document(uid).collection("tasks")
                .addSnapshotListener((query, e) -> {
                    if (e != null || query == null) {
                        Log.e(TAG, "Tasks progress fetch failed", e);
                        return;
                    }
                    Map<String, SubjectTaskStats> local = new HashMap<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String subject = normalizeSubject(doc.getString("subject"));
                        if (subject == null) continue;
                        SubjectTaskStats stats = local.containsKey(subject) ? local.get(subject) : new SubjectTaskStats();
                        stats.total++;
                        if (Boolean.TRUE.equals(doc.getBoolean("completed"))) stats.done++;
                        local.put(subject, stats);
                    }
                    synchronized (subjectTaskStatsMap) {
                        subjectTaskStatsMap.clear();
                        subjectTaskStatsMap.putAll(local);
                    }
                    renderProgressUi();
                }));

        listeners.add(firestore.collection("users").document(uid).collection("exams")
                .addSnapshotListener((query, e) -> {
                    if (e != null || query == null) {
                        Log.e(TAG, "Exams progress fetch failed", e);
                        return;
                    }
                    Map<String, SubjectExamStats> local = new HashMap<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String subject = normalizeSubject(doc.getString("subject"));
                        if (subject == null) {
                            subject = inferSubjectFromTitle(doc.getString("title"));
                        }
                        if (subject == null) continue;
                        SubjectExamStats stats = local.containsKey(subject) ? local.get(subject) : new SubjectExamStats();
                        stats.total++;
                        stats.sumProgress += parseProgress(doc.get("progress"));
                        local.put(subject, stats);
                    }
                    synchronized (subjectExamStatsMap) {
                        subjectExamStatsMap.clear();
                        subjectExamStatsMap.putAll(local);
                    }
                    renderProgressUi();
                }));
    }

    private Integer parseProgress(Object raw) {
        if (raw == null) return 0;
        if (raw instanceof Long) return Math.max(0, Math.min(100, ((Long) raw).intValue()));
        if (raw instanceof Integer) return Math.max(0, Math.min(100, (Integer) raw));
        try {
            return Math.max(0, Math.min(100, Integer.parseInt(String.valueOf(raw))));
        } catch (Exception ignore) {
            return 0;
        }
    }

    private void renderProgressUi() {
        runOnUiThread(() -> {
            Map<String, Integer> data = buildComputedSubjectProgress();
            if (data.isEmpty()) {
                data.put("Subject 1", 0);
            }

            int average = 0;
            for (Integer p : data.values()) average += p;
            average = average / Math.max(1, data.size());

            float thisWeek = average * 0.35f;
            float lastWeek = Math.max(0f, thisWeek - 2.5f);
            tvThisWeekHours.setText(String.format(Locale.getDefault(), "%.1fh", thisWeek));
            tvLastWeekHours.setText(String.format(Locale.getDefault(), "%.1fh", lastWeek));

            renderWeeklyGraph(data);
            renderMonthlyGraph(data);
            renderSubjectCards(data);
        });
    }

    private Map<String, Integer> buildComputedSubjectProgress() {
        Map<String, Integer> result = new HashMap<>();
        List<String> names = new ArrayList<>(orderedSubjectNames);
        names.sort(Comparator.naturalOrder());
        if (names.size() > 5) {
            names = names.subList(0, 5);
        }

        for (String subject : names) {
            int base = subjectBaseProgressMap.containsKey(subject) ? subjectBaseProgressMap.get(subject) : 0;
            SubjectTaskStats taskStats = subjectTaskStatsMap.get(subject);
            SubjectExamStats examStats = subjectExamStatsMap.get(subject);
            int taskProgress = (taskStats == null || taskStats.total == 0) ? base : (taskStats.done * 100 / taskStats.total);
            int examProgress = (examStats == null || examStats.total == 0) ? base : (examStats.sumProgress / examStats.total);

            int computed;
            if (taskStats != null && taskStats.total > 0 && examStats != null && examStats.total > 0) {
                computed = (base + taskProgress + examProgress) / 3;
            } else if (taskStats != null && taskStats.total > 0) {
                computed = (base + taskProgress) / 2;
            } else if (examStats != null && examStats.total > 0) {
                computed = (base + examProgress) / 2;
            } else {
                computed = base;
            }
            result.put(subject, Math.max(0, Math.min(100, computed)));
        }
        return result;
    }

    private void renderWeeklyGraph(Map<String, Integer> data) {
        weeklyGraphContainer.removeAllViews();
        weeklyGraphContainer.setGravity(android.view.Gravity.BOTTOM);

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            LinearLayout barWrap = new LinearLayout(this);
            barWrap.setOrientation(LinearLayout.VERTICAL);
            barWrap.setGravity(android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            wrapParams.setMargins(dpToPx(2), 0, dpToPx(2), 0);
            barWrap.setLayoutParams(wrapParams);

            View bar = new View(this);
            int barHeight = dpToPx(20 + (entry.getValue() * 80 / 100));
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(dpToPx(14), barHeight);
            bar.setLayoutParams(barParams);
            bar.setBackgroundColor(0xFFB39DDB);

            TextView label = new TextView(this);
            label.setText(entry.getKey().length() > 4 ? entry.getKey().substring(0, 4) : entry.getKey());
            label.setTextSize(10f);
            label.setPadding(0, dpToPx(4), 0, 0);

            barWrap.addView(bar);
            barWrap.addView(label);
            weeklyGraphContainer.addView(barWrap);
        }
    }

    private void renderMonthlyGraph(Map<String, Integer> data) {
        monthlyGraphContainer.removeAllViews();
        monthlyGraphContainer.setOrientation(LinearLayout.VERTICAL);
        monthlyGraphContainer.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            TextView row = new TextView(this);
            row.setText(entry.getKey() + "  •  " + entry.getValue() + "%");
            row.setTextSize(13f);
            row.setPadding(0, dpToPx(4), 0, dpToPx(4));
            monthlyGraphContainer.addView(row);
        }
    }

    private void renderSubjectCards(Map<String, Integer> data) {
        subjectProgressContainer.removeAllViews();
        List<Map.Entry<String, Integer>> ordered = new ArrayList<>(data.entrySet());
        ordered.sort(Map.Entry.comparingByKey());
        for (Map.Entry<String, Integer> entry : ordered) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(cardDrawables[random.nextInt(cardDrawables.length)]);
            card.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(dpToPx(24), dpToPx(8), dpToPx(24), dpToPx(4));
            card.setLayoutParams(params);

            LinearLayout top = new LinearLayout(this);
            top.setOrientation(LinearLayout.HORIZONTAL);

            TextView name = new TextView(this);
            name.setText(entry.getKey());
            name.setTextSize(15f);
            name.setTypeface(null, android.graphics.Typeface.BOLD);
            name.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView value = new TextView(this);
            value.setText(entry.getValue() + "%");
            value.setTextSize(14f);
            value.setTypeface(null, android.graphics.Typeface.BOLD);

            ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            bar.setMax(100);
            bar.setProgress(Math.max(0, Math.min(100, entry.getValue())));
            bar.setProgressDrawable(getDrawable(R.drawable.progress_bar));
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(8));
            barParams.setMargins(0, dpToPx(10), 0, 0);
            bar.setLayoutParams(barParams);

            top.addView(name);
            top.addView(value);
            card.addView(top);
            card.addView(bar);
            subjectProgressContainer.addView(card);
        }
    }

    private String normalizeSubject(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        String value = raw.trim();
        for (String known : orderedSubjectNames) {
            if (known.equalsIgnoreCase(value)) return known;
        }
        return value;
    }

    private String inferSubjectFromTitle(String title) {
        if (title == null) return null;
        String low = title.toLowerCase(Locale.getDefault());
        for (String known : orderedSubjectNames) {
            if (low.contains(known.toLowerCase(Locale.getDefault()))) {
                return known;
            }
        }
        return null;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (ListenerRegistration registration : listeners) {
            if (registration != null) registration.remove();
        }
        listeners.clear();
    }

    private static class SubjectTaskStats {
        int total = 0;
        int done = 0;
    }

    private static class SubjectExamStats {
        int total = 0;
        int sumProgress = 0;
    }
}