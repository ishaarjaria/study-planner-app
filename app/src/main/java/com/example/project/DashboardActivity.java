package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

public class DashboardActivity extends AppCompatActivity {

    TextView btnBack, navDashboard, navCalendar, navTasks, navProgress, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Back button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, LandingActivity.class));
            finish();
        });

        // Bottom navigation
        LinearLayout navDashboard = findViewById(R.id.navDashboard);
        LinearLayout navCalendar = findViewById(R.id.navCalendar);
        LinearLayout navTasks = findViewById(R.id.navTasks);
        LinearLayout navProgress = findViewById(R.id.navProgress);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        // Already on Dashboard → do nothing
        navDashboard.setOnClickListener(v -> { });

        navCalendar.setOnClickListener(v ->
                startActivity(new Intent(this, CalendarActivity.class)));

        navTasks.setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        navProgress.setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        LinearLayout btnAddTask = findViewById(R.id.btnAddTask);
        LinearLayout btnTimer = findViewById(R.id.btnTimer);
        LinearLayout btnTimetable = findViewById(R.id.btnTimetable);

        btnAddTask.setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        btnTimer.setOnClickListener(v ->
                startActivity(new Intent(this, StudyTimerActivity.class)));

        btnTimetable.setOnClickListener(v ->
                startActivity(new Intent(this, TimetableActivity.class)));
    }
}