package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

public class TimetableResultActivity extends AppCompatActivity {

    TextView btnBack, navDashboard, navCalendar, navTasks, navProgress, navProfile;
    Button btnRegenerate, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_result);

        // 🔗 Link Views
        btnBack = findViewById(R.id.btnBack);

        LinearLayout navDashboard = findViewById(R.id.navDashboard);
        LinearLayout navCalendar = findViewById(R.id.navCalendar);
        LinearLayout navTasks = findViewById(R.id.navTasks);
        LinearLayout navProgress = findViewById(R.id.navProgress);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        btnRegenerate = findViewById(R.id.btnRegenerate);
        btnSave = findViewById(R.id.btnSave);

        // 🔙 Back to previous screen
        btnBack.setOnClickListener(v -> finish());

        // 🔁 Regenerate → go back to input page
        btnRegenerate.setOnClickListener(v -> {
            finish(); // returns to TimetableActivity
        });

        // 💾 Save Schedule
        btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "Schedule Saved!", Toast.LENGTH_SHORT).show();
        });

        // 🔽 Bottom Navigation
        navDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        navCalendar.setOnClickListener(v ->
                startActivity(new Intent(this, CalendarActivity.class)));

        navTasks.setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        navProgress.setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}