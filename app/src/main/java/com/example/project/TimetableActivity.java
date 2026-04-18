package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

public class TimetableActivity extends AppCompatActivity {

    EditText etSubjects, etDate, etHours, etPriority;
    Button btnGenerate;
    TextView btnBack, navDashboard, navCalendar, navTasks, navProgress, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        // 🔗 Link UI
        etSubjects = findViewById(R.id.etSubjects);
        etDate = findViewById(R.id.etDate);
        etHours = findViewById(R.id.etHours);
        etPriority = findViewById(R.id.etPriority);

        btnGenerate = findViewById(R.id.btnGenerate);
        btnBack = findViewById(R.id.btnBack);

        LinearLayout navDashboard = findViewById(R.id.navDashboard);
        LinearLayout navCalendar = findViewById(R.id.navCalendar);
        LinearLayout navTasks = findViewById(R.id.navTasks);
        LinearLayout navProgress = findViewById(R.id.navProgress);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        // 🔙 Back button
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // 🚀 Generate Button
        btnGenerate.setOnClickListener(v -> {

            String subjects = etSubjects.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String hours = etHours.getText().toString().trim();
            String priority = etPriority.getText().toString().trim();

            if (subjects.isEmpty() || date.isEmpty() || hours.isEmpty() || priority.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // 🔥 OPEN RESULT PAGE
                startActivity(new Intent(this, TimetableResultActivity.class));
            }
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