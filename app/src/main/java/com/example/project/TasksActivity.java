package com.example.project;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TasksActivity extends AppCompatActivity {

    CheckBox cb1, cb2, cb3, cb4, cb5;
    TextView tv1, tv2, tv3, tv4, tv5;

    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    TextView btnBack;

    FloatingActionButton btnAddTask;

    LinearLayout taskContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

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

    // Add task dynamically
    private void addNewTask(String taskText) {

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

        cb.setOnCheckedChangeListener((b, isChecked) -> {
            if (isChecked) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });

        card.addView(cb);
        card.addView(tv);

        taskContainer.addView(card);
    }
}