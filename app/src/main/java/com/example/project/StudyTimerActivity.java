package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StudyTimerActivity extends AppCompatActivity {

    TextView tvTimer, tvSessions, tvMinutes;
    ImageButton btnStart, btnReset;

    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    TextView btnBack;

    CountDownTimer timer;

    long timeLeft = 1500000; // 25 min
    boolean isRunning = false;

    int sessions = 0;
    int minutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_timer);

        tvTimer = findViewById(R.id.tvTimer);
        tvSessions = findViewById(R.id.tvSessions);
        tvMinutes = findViewById(R.id.tvMinutes);

        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);

        btnBack = findViewById(R.id.btnBack);

        navDashboard = findViewById(R.id.navDashboard);
        navCalendar = findViewById(R.id.navCalendar);
        navTasks = findViewById(R.id.navTasks);
        navProgress = findViewById(R.id.navProgress);
        navProfile = findViewById(R.id.navProfile);


        btnStart.setOnClickListener(v -> {
            if (!isRunning) startTimer();
        });

        btnReset.setOnClickListener(v -> resetTimer());

        updateTimerText();
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

    private void startTimer() {
        timer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isRunning = false;

                sessions++;
                minutes += 25;

                tvSessions.setText(String.valueOf(sessions));
                tvMinutes.setText(String.valueOf(minutes));
            }
        }.start();

        isRunning = true;
    }

    private void resetTimer() {
        if (timer != null) timer.cancel();

        timeLeft = 1500000;
        isRunning = false;
        updateTimerText();
    }

    private void updateTimerText() {
        int minutesVal = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        String time = String.format("%02d:%02d", minutesVal, seconds);
        tvTimer.setText(time);
    }
}