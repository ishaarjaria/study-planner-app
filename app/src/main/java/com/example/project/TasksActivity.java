package com.example.project;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TasksActivity extends AppCompatActivity {
    private static final String TAG = "TasksActivity";

    CheckBox cb1, cb2, cb3, cb4, cb5;
    TextView tv1, tv2, tv3, tv4, tv5;

    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    TextView btnBack;

    FloatingActionButton btnAddTask;

    LinearLayout taskContainer;
    LinearLayout firebaseTaskContainer;
    private TextView tvActiveCount;
    private TextView tvDoneCount;
    private TextView tvTotalCount;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Views
        cb1 = findViewById(R.id.cbTask1);
        cb2 = findViewById(R.id.cbTask2);
        cb3 = findViewById(R.id.cbTask3);
        cb4 = findViewById(R.id.cbTask4);
        cb5 = findViewById(R.id.cbTask5);

        tv1 = findViewById(R.id.tvTask1);
        tv2 = findViewById(R.id.tvTask2);
        tv3 = findViewById(R.id.tvTask3);
        tv4 = findViewById(R.id.tvTask4);
        tv5 = findViewById(R.id.tvTask5);

        taskContainer = findViewById(R.id.taskContainer);
        firebaseTaskContainer = findViewById(R.id.firebaseTaskContainer);
        tvActiveCount = findViewById(R.id.tvActiveCount);
        tvDoneCount = findViewById(R.id.tvDoneCount);
        tvTotalCount = findViewById(R.id.tvTotalCount);

        btnAddTask = findViewById(R.id.btnAddTask);
        btnBack = findViewById(R.id.btnBack);

        navDashboard = findViewById(R.id.navDashboard);
        navCalendar = findViewById(R.id.navCalendar);
        navTasks = findViewById(R.id.navTasks);
        navProgress = findViewById(R.id.navProgress);
        navProfile = findViewById(R.id.navProfile);

        // Strike logic
        setupStrike(cb1, tv1);
        setupStrike(cb2, tv2);
        setupStrike(cb3, tv3);
        setupStrike(cb4, tv4);
        setupStrike(cb5, tv5);

        // Add Task button
        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTaskActivity.class);
            startActivityForResult(intent, 1);
        });

        // Back
        btnBack.setOnClickListener(v -> finish());

        // Navigation
        navDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        navCalendar.setOnClickListener(v ->
                startActivity(new Intent(this, CalendarActivity.class)));

        navTasks.setOnClickListener(v -> {});

        navProgress.setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadTasksFromFirebase();
    }

    private void setupStrike(CheckBox cb, TextView tv) {
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });
    }

    // Receive new task
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String task = data.getStringExtra("task");
            addNewTask(task);
        }
    }

    private void addNewTask(String taskText) {
        addTaskCard(taskText, false, null);
    }

    private void loadTasksFromFirebase() {
        if (currentUser == null) {
            Log.e(TAG, "User is null while loading tasks");
            return;
        }
        firestore.collection("users").document(currentUser.getUid()).collection("tasks")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    firebaseTaskContainer.removeAllViews();
                    int done = 0;
                    int total = queryDocumentSnapshots.size();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String taskText = doc.getString("title");
                        Boolean completed = doc.getBoolean("completed");
                        if (Boolean.TRUE.equals(completed)) {
                            done++;
                        }
                        addTaskCard(taskText == null ? "Untitled task" : taskText,
                                Boolean.TRUE.equals(completed),
                                doc.getId());
                    }
                    int active = Math.max(0, total - done);
                    tvActiveCount.setText(String.valueOf(active));
                    tvDoneCount.setText(String.valueOf(done));
                    tvTotalCount.setText(String.valueOf(total));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch tasks", e));
    }

    private void addTaskCard(String taskText, boolean initialChecked, String taskId) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(30, 30, 30, 30);
        card.setBackgroundResource(R.drawable.card_purple);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 20, 0, 0);
        card.setLayoutParams(params);

        CheckBox cb = new CheckBox(this);

        TextView tv = new TextView(this);
        tv.setText(taskText);
        tv.setPadding(20, 0, 0, 0);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
        ));

        cb.setOnCheckedChangeListener((b, isChecked) -> {
            if (isChecked) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            if (taskId != null && currentUser != null) {
                firestore.collection("users").document(currentUser.getUid()).collection("tasks")
                        .document(taskId)
                        .update("completed", isChecked, "updatedAt", System.currentTimeMillis())
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to update task state", e));
            }
        });
        cb.setChecked(initialChecked);

        card.addView(cb);
        card.addView(tv);

        if (taskId == null) {
            taskContainer.addView(card);
        } else {
            firebaseTaskContainer.addView(card);
        }
    }
}