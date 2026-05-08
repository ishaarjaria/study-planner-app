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
    TextView btnMode25, btnMode15, btnModeBreak5;

    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    TextView btnBack;

    CountDownTimer timer;

    long selectedDuration = 1500000;
    long timeLeft = 1500000;
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
        btnMode25 = findViewById(R.id.btnMode25);
        btnMode15 = findViewById(R.id.btnMode15);
        btnModeBreak5 = findViewById(R.id.btnModeBreak5);

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
        btnMode25.setOnClickListener(v -> setMode(25 * 60 * 1000L));
        btnMode15.setOnClickListener(v -> setMode(15 * 60 * 1000L));
        btnModeBreak5.setOnClickListener(v -> setMode(5 * 60 * 1000L));

        updateTimerText();
        // Back
        btnBack.setOnClickListener(v -> navigateToLanding());

        // Navigation
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

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
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
                minutes += (int) (selectedDuration / 60000L);
                timeLeft = selectedDuration;
                updateTimerText();

                tvSessions.setText(String.valueOf(sessions));
                tvMinutes.setText(String.valueOf(minutes));
            }
        }.start();

        isRunning = true;
    }

    private void resetTimer() {
        if (timer != null) timer.cancel();

        timeLeft = selectedDuration;
        isRunning = false;
        updateTimerText();
    }

    private void setMode(long durationMillis) {
        selectedDuration = durationMillis;
        resetTimer();
    }

    private void updateTimerText() {
        int minutesVal = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        String time = String.format("%02d:%02d", minutesVal, seconds);
        tvTimer.setText(time);
    }

    private void navigateToLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}