package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RemindersActivity extends AppCompatActivity {

    Button btnAddReminder;
    LinearLayout timelineContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        btnAddReminder = findViewById(R.id.btnAddReminder);
        timelineContainer = findViewById(R.id.timelineContainer);

        btnAddReminder.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddReminderActivity.class);
            startActivityForResult(intent, 1);
        });
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