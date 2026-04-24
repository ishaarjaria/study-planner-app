package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SubjectsActivity extends AppCompatActivity {

    Button btnAddSubject;
    LinearLayout subjectsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        btnAddSubject = findViewById(R.id.btnAddSubject);
        subjectsContainer = findViewById(R.id.subjectsContainer);

        // ➕ Open Add Subject
        btnAddSubject.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddSubjectActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    // 🔥 Receive new subject
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            String name = data.getStringExtra("name");
            String level = data.getStringExtra("level");
            String progress = data.getStringExtra("progress");
            String hours = data.getStringExtra("hours");

            addSubject(name, level, progress, hours);
        }
    }

    // 🔥 Add subject dynamically
    private void addSubject(String name, String level, String progress, String hours) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 20, 20, 20);
        card.setBackgroundResource(R.drawable.card_blue);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 20);
        card.setLayoutParams(params);

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextSize(16);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvLevel = new TextView(this);
        tvLevel.setText(level);

        TextView tvProgress = new TextView(this);
        tvProgress.setText("Progress: " + progress + "%");

        TextView tvHours = new TextView(this);
        tvHours.setText("Study Time: " + hours + "h");

        card.addView(tvName);
        card.addView(tvLevel);
        card.addView(tvProgress);
        card.addView(tvHours);

        subjectsContainer.addView(card);
    }
}