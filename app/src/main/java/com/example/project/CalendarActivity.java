package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    TextView btnBack;

    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // 🔗 Link Views
        calendarView = findViewById(R.id.calendarView);
        btnBack = findViewById(R.id.btnBack);

        navDashboard = findViewById(R.id.navDashboard);
        navCalendar = findViewById(R.id.navCalendar);
        navTasks = findViewById(R.id.navTasks);
        navProgress = findViewById(R.id.navProgress);
        navProfile = findViewById(R.id.navProfile);

        // ✅ FIX: Set current date (solves 1970 problem)
        calendarView.setDate(System.currentTimeMillis(), true, true);

        // 📅 Date click listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Toast.makeText(this,
                    "Selected: " + dayOfMonth + "/" + (month + 1) + "/" + year,
                    Toast.LENGTH_SHORT).show();
        });

        // 🔙 Back button
        btnBack.setOnClickListener(v -> finish());

        // 🔽 Bottom Navigation

        navDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        navCalendar.setOnClickListener(v ->
                Toast.makeText(this, "Already on Calendar", Toast.LENGTH_SHORT).show());

        navTasks.setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        navProgress.setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}