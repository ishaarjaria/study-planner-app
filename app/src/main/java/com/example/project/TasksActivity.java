package com.example.project;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
<<<<<<< HEAD
import android.view.ViewGroup;
=======
>>>>>>> 58c259c (new changes)
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
<<<<<<< HEAD
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
=======
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
>>>>>>> 58c259c (new changes)

public class TasksActivity extends AppCompatActivity {
    private static final String TAG = "TasksActivity";

    CheckBox cb1, cb2, cb3, cb4, cb5;
    TextView tv1, tv2, tv3, tv4, tv5;

    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    TextView btnBack;

    FloatingActionButton btnAddTask;

    LinearLayout taskContainer;
<<<<<<< HEAD
    LinearLayout firebaseTaskContainer;
=======
>>>>>>> 58c259c (new changes)
    private TextView tvActiveCount;
    private TextView tvDoneCount;
    private TextView tvTotalCount;
    private FirebaseFirestore firestore;
<<<<<<< HEAD
    private FirebaseUser currentUser;
=======
    private String uid;
    private ListenerRegistration tasksListener;
    private static final String TAG = "TasksActivity";
>>>>>>> 58c259c (new changes)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        firestore = FirebaseFirestore.getInstance();
<<<<<<< HEAD
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
=======
        uid = FirebaseAuth.getInstance().getCurrentUser() == null
                ? null : FirebaseAuth.getInstance().getCurrentUser().getUid();
>>>>>>> 58c259c (new changes)

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
<<<<<<< HEAD
        firebaseTaskContainer = findViewById(R.id.firebaseTaskContainer);
=======
>>>>>>> 58c259c (new changes)
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

        if (uid == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        observeTasksRealtime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (uid != null && tasksListener == null) {
            observeTasksRealtime();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tasksListener != null) {
            tasksListener.remove();
            tasksListener = null;
        }
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
            loadTasksFromFirebase();
        }
    }

<<<<<<< HEAD
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
=======
    private void addNewTask(String taskId, String taskText, boolean completed) {
>>>>>>> 58c259c (new changes)
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
        cb.setChecked(completed);

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
<<<<<<< HEAD
            if (taskId != null && currentUser != null) {
                firestore.collection("users").document(currentUser.getUid()).collection("tasks")
                        .document(taskId)
                        .update("completed", isChecked, "updatedAt", System.currentTimeMillis())
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to update task state", e));
            }
=======
            firestore.collection("tasks").document(taskId)
                    .update("completed", isChecked)
                    .addOnSuccessListener(unused -> Log.d(TAG, "Dynamic task updated: " + taskId + " -> " + isChecked))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update task status", e));
>>>>>>> 58c259c (new changes)
        });
        cb.setChecked(initialChecked);

        if (completed) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        card.addView(cb);
        card.addView(tv);

        if (taskId == null) {
            taskContainer.addView(card);
        } else {
            firebaseTaskContainer.addView(card);
        }
    }

    private void bindStaticTask(CheckBox cb, TextView tv, String taskId, String title, boolean completed) {
        cb.setOnCheckedChangeListener(null);
        tv.setText(title);
        cb.setChecked(completed);
        if (completed) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            firestore.collection("tasks").document(taskId)
                    .update("completed", isChecked)
                    .addOnSuccessListener(unused -> Log.d(TAG, "Static task updated: " + taskId + " -> " + isChecked))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update static task", e));
        });
    }

    private void observeTasksRealtime() {
        tasksListener = firestore.collection("tasks")
                .whereEqualTo("uid", uid)
                .orderBy("createdAt")
                .addSnapshotListener((query, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Realtime task listener failed", e);
                        return;
                    }
                    if (query == null) {
                        Log.e(TAG, "Realtime task listener returned null query");
                        return;
                    }
                    bindTasks(query.getDocuments());
                });
    }

    private void loadTasksFromFirebase() {
        firestore.collection("tasks")
                .whereEqualTo("uid", uid)
                .orderBy("createdAt")
                .get()
                .addOnSuccessListener(query -> {
                    bindTasks(query.getDocuments());
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch tasks", e));
    }

    private void bindTasks(List<DocumentSnapshot> docs) {
        List<LinearLayout> dynamicCards = new ArrayList<>();
        for (int i = 0; i < taskContainer.getChildCount(); i++) {
            if ("dynamic_task".equals(String.valueOf(taskContainer.getChildAt(i).getTag()))) {
                dynamicCards.add((LinearLayout) taskContainer.getChildAt(i));
            }
        }
        for (LinearLayout card : dynamicCards) {
            taskContainer.removeView(card);
        }

        CheckBox[] checkBoxes = {cb1, cb2, cb3, cb4, cb5};
        TextView[] titles = {tv1, tv2, tv3, tv4, tv5};
        for (int i = 0; i < 5; i++) {
            if (i < docs.size()) {
                DocumentSnapshot doc = docs.get(i);
                String title = doc.getString("title");
                Boolean completed = doc.getBoolean("completed");
                bindStaticTask(checkBoxes[i], titles[i], doc.getId(),
                        title == null ? "Untitled Task" : title,
                        Boolean.TRUE.equals(completed));
            } else {
                bindStaticTask(checkBoxes[i], titles[i], "", "No task", false);
                checkBoxes[i].setOnCheckedChangeListener(null);
            }
        }
        int completedCount = 0;
        for (int i = 0; i < docs.size(); i++) {
            DocumentSnapshot doc = docs.get(i);
            Boolean completed = doc.getBoolean("completed");
            if (Boolean.TRUE.equals(completed)) {
                completedCount++;
            }
            if (i >= 5) {
                String title = doc.getString("title");
                addNewTask(doc.getId(),
                        title == null ? "Untitled Task" : title,
                        Boolean.TRUE.equals(completed));
                if (taskContainer.getChildCount() > 0) {
                    taskContainer.getChildAt(taskContainer.getChildCount() - 1).setTag("dynamic_task");
                }
            }
        }
        int total = docs.size();
        int active = Math.max(0, total - completedCount);
        tvActiveCount.setText(String.valueOf(active));
        tvDoneCount.setText(String.valueOf(completedCount));
        tvTotalCount.setText(String.valueOf(total));
        Log.d(TAG, "Counters updated active=" + active + ", done=" + completedCount + ", total=" + total);
    }
}