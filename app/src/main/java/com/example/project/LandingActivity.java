package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        findViewById(R.id.btn1).setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        findViewById(R.id.btn2).setOnClickListener(v -> startActivity(new Intent(this, TimetableActivity.class)));
        findViewById(R.id.btn3).setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        findViewById(R.id.btn4).setOnClickListener(v -> startActivity(new Intent(this, TasksActivity.class)));
        findViewById(R.id.btn5).setOnClickListener(v -> startActivity(new Intent(this, StudyTimerActivity.class)));
        findViewById(R.id.btn6).setOnClickListener(v -> startActivity(new Intent(this, ExamCountdownActivity.class)));
        findViewById(R.id.btn7).setOnClickListener(v -> startActivity(new Intent(this, RemindersActivity.class)));
        findViewById(R.id.btn8).setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        findViewById(R.id.btn9).setOnClickListener(v -> startActivity(new Intent(this, NotesActivity.class)));
        findViewById(R.id.btn10).setOnClickListener(v -> startActivity(new Intent(this, SubjectsActivity.class)));
        findViewById(R.id.btn11).setOnClickListener(v -> startActivity(new Intent(this, SuggestionsActivity.class)));
        findViewById(R.id.btn12).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }
}