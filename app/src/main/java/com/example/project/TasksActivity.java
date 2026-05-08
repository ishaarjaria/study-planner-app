package com.example.project;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Random;

public class TasksActivity extends AppCompatActivity {

    private static final String TAG = "TasksActivity";

    private LinearLayout taskContainer;
    private TextView tvActiveCount, tvDoneCount, tvTotalCount;
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
        setContentView(R.layout.activity_tasks);

        firestore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser() == null
                ? null
                : FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (uid == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        taskContainer = findViewById(R.id.taskContainer);
        tvActiveCount = findViewById(R.id.tvActiveCount);
        tvDoneCount = findViewById(R.id.tvDoneCount);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        btnBack = findViewById(R.id.btnBack);

        FloatingActionButton btnAddTask = findViewById(R.id.btnAddTask);
        btnAddTask.setOnClickListener(v ->
                startActivity(new Intent(this, AddTaskActivity.class)));

        setupBottomNavigation();
        btnBack.setOnClickListener(v -> navigateToLanding());

        observeTasks();
    }

    private void setupBottomNavigation() {
        setNavClick(R.id.navDashboard, DashboardActivity.class, false);
        setNavClick(R.id.navCalendar, CalendarActivity.class, false);
        setNavClick(R.id.navTasks, TasksActivity.class, true);
        setNavClick(R.id.navProgress, ProgressActivity.class, false);
        setNavClick(R.id.navProfile, ProfileActivity.class, false);
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

    private void observeTasks() {
        listener = firestore.collection("users")
                .document(uid)
                .collection("tasks")
                .addSnapshotListener((query, e) -> {

                    if (e != null || query == null) {
                        Log.e(TAG, "Error loading tasks", e);
                        return;
                    }

                    taskContainer.removeAllViews();

                    int total = query.size();
                    int done = 0;

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        String title = doc.getString("title");
                        Boolean completed = doc.getBoolean("completed");

                        if (Boolean.TRUE.equals(completed)) done++;

                        addTaskCard(
                                doc.getId(),
                                title == null ? "Untitled Task" : title,
                                Boolean.TRUE.equals(completed)
                        );
                    }

                    int active = total - done;

                    tvActiveCount.setText(String.valueOf(active));
                    tvDoneCount.setText(String.valueOf(done));
                    tvTotalCount.setText(String.valueOf(total));
                });
    }

    private void addTaskCard(String id, String text, boolean checked) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        int cardPadding = dpToPx(16);
        card.setPadding(cardPadding, cardPadding, cardPadding, cardPadding);
        card.setBackgroundResource(cardDrawables[random.nextInt(cardDrawables.length)]);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(24), dpToPx(14), dpToPx(24), 0);
        card.setLayoutParams(params);

        CheckBox cb = new CheckBox(this);
        cb.setChecked(checked);

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setTextSize(15f);
        tv.setPadding(dpToPx(12), 0, 0, 0);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
        ));

        if (checked) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        cb.setOnCheckedChangeListener((b, isChecked) -> {

            if (isChecked) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            firestore.collection("users")
                    .document(uid)
                    .collection("tasks")
                    .document(id)
                    .update("completed", isChecked)
                    .addOnSuccessListener(unused ->
                            Log.d(TAG, "Task completion updated: " + id + " -> " + isChecked))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Task completion update failed for " + id, e);
                        Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
                    });
        });

        card.addView(cb);
        card.addView(tv);

        taskContainer.addView(card);
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
        if (listener != null) listener.remove();
    }
}