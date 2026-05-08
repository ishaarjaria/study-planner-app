package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.Random;

public class RemindersActivity extends AppCompatActivity {

    Button btnAddReminder;
    LinearLayout upcomingContainer, completedContainer;
    TextView btnBack;
    TextView tvUpcomingCount, tvCompletedCount;
    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    private static final String TAG = "RemindersActivity";
    private ListenerRegistration reminderListener;
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
        setContentView(R.layout.activity_reminders);

        btnAddReminder = findViewById(R.id.btnAddReminder);
        upcomingContainer = findViewById(R.id.upcomingContainer);
        completedContainer = findViewById(R.id.completedContainer);
        btnBack = findViewById(R.id.btnBack);
        tvUpcomingCount = findViewById(R.id.tvUpcomingCount);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        navDashboard = findViewById(R.id.navDashboard);
        navCalendar = findViewById(R.id.navCalendar);
        navTasks = findViewById(R.id.navTasks);
        navProgress = findViewById(R.id.navProgress);
        navProfile = findViewById(R.id.navProfile);

        btnAddReminder.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddReminderActivity.class);
            startActivityForResult(intent, 1);
        });

        btnBack.setOnClickListener(v -> navigateToLanding());
        navDashboard.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        navCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        navTasks.setOnClickListener(v -> startActivity(new Intent(this, TasksActivity.class)));
        navProgress.setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        observeReminders();
    }

    private void addReminderCard(String id, String title, String subject, String time, boolean completed) {
        Log.d(TAG, "Adding reminder title=" + title + ", subject=" + subject + ", time=" + time + ", completed=" + completed);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(cardDrawables[random.nextInt(cardDrawables.length)]);
        int pad = dpToPx(16);
        card.setPadding(pad, pad, pad, pad);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(24), 0, dpToPx(24), dpToPx(12));
        card.setLayoutParams(params);

        TextView tvTime = new TextView(this);
        tvTime.setText(time == null ? "No time" : time);
        tvTime.setTextSize(12f);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title == null ? "Reminder" : title);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextSize(15f);
        tvTitle.setPadding(0, dpToPx(6), 0, 0);

        TextView tvSub = new TextView(this);
        tvSub.setText(subject == null ? "" : subject);
        tvSub.setTextSize(13f);
        tvSub.setPadding(0, dpToPx(4), 0, 0);

        CheckBox cbCompleted = new CheckBox(this);
        cbCompleted.setText("Completed");
        cbCompleted.setChecked(completed);
        cbCompleted.setPadding(0, dpToPx(6), 0, 0);
        cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> updateReminderCompletion(id, isChecked));

        card.addView(tvTime);
        card.addView(tvTitle);
        card.addView(tvSub);
        card.addView(cbCompleted);

        if (completed) {
            completedContainer.addView(card);
        } else {
            upcomingContainer.addView(card);
        }
    }

    private void observeReminders() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reminderListener = FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .collection("reminders")
                .addSnapshotListener((query, e) -> {
                    if (e != null || query == null) {
                        Log.e(TAG, "Reminder load failed", e);
                        return;
                    }
                    upcomingContainer.removeAllViews();
                    completedContainer.removeAllViews();
                    int upcomingCount = 0;
                    int completedCount = 0;
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        boolean completed = Boolean.TRUE.equals(doc.getBoolean("completed"));
                        if (completed) {
                            completedCount++;
                        } else {
                            upcomingCount++;
                        }
                        addReminderCard(
                                doc.getId(),
                                doc.getString("title"),
                                doc.getString("subject"),
                                doc.getString("time"),
                                completed
                        );
                    }
                    tvUpcomingCount.setText(String.valueOf(upcomingCount));
                    tvCompletedCount.setText(String.valueOf(completedCount));
                });
    }

    private void updateReminderCompletion(String reminderId, boolean completed) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .collection("reminders")
                .document(reminderId)
                .update("completed", completed)
                .addOnSuccessListener(unused -> Log.d(TAG, "Reminder updated: " + reminderId + " -> " + completed))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update reminder: " + reminderId, e));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reminderListener != null) reminderListener.remove();
    }
}