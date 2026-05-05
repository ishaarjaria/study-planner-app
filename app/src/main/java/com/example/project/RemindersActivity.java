package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RemindersActivity extends AppCompatActivity {

    Button btnAddReminder;
    LinearLayout timelineContainer;
    TextView btnBack;
    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    private static final String TAG = "RemindersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        btnAddReminder = findViewById(R.id.btnAddReminder);
        timelineContainer = findViewById(R.id.timelineContainer);
        btnBack = findViewById(R.id.btnBack);
        navDashboard = findViewById(R.id.navDashboard);
        navCalendar = findViewById(R.id.navCalendar);
        navTasks = findViewById(R.id.navTasks);
        navProgress = findViewById(R.id.navProgress);
        navProfile = findViewById(R.id.navProfile);

        btnAddReminder.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddReminderActivity.class);
            startActivityForResult(intent, 1);
        });

        btnBack.setOnClickListener(v -> finish());
        navDashboard.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        navCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        navTasks.setOnClickListener(v -> startActivity(new Intent(this, TasksActivity.class)));
        navProgress.setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String title = data.getStringExtra("title");
            String subject = data.getStringExtra("subject");
            String time = data.getStringExtra("time");

            addReminder(title, subject, time);
        }
    }

    private void addReminder(String title, String subject, String time) {
        Log.d(TAG, "Adding reminder title=" + title + ", subject=" + subject + ", time=" + time);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 10, 0, 10);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.card_blue);
        card.setPadding(20, 20, 20, 20);

        TextView tvTime = new TextView(this);
        tvTime.setText(time);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);

        TextView tvSub = new TextView(this);
        tvSub.setText(subject);

        card.addView(tvTime);
        card.addView(tvTitle);
        card.addView(tvSub);

        row.addView(card);

        timelineContainer.addView(row);
    }
}