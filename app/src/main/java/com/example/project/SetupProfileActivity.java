package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SetupProfileActivity extends AppCompatActivity {

    EditText etSubject;
    Button btnAdd, btnNext;

    LinearLayout subjectsContainer, btnBack;

    TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        // INIT
        etSubject = findViewById(R.id.etSubject);
        btnAdd = findViewById(R.id.btnAdd);
        btnNext = findViewById(R.id.btnNext);

        subjectsContainer = findViewById(R.id.subjectsContainer);
        btnBack = findViewById(R.id.btnBack);

        tvEmpty = findViewById(R.id.tvEmpty);

        // ADD SUBJECT
        btnAdd.setOnClickListener(v -> addSubject());

        // NEXT
        btnNext.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Next Step",
                    Toast.LENGTH_SHORT
            ).show();

            // Example:
            // startActivity(new Intent(this, NextActivity.class));
        });

        // BACK
        btnNext.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SetupProfileActivity.this,
                    SetupProfile2Activity.class
            );

            startActivity(intent);
        });
    }

    private void addSubject() {

        String subject = etSubject.getText().toString().trim();

        if (TextUtils.isEmpty(subject)) {

            etSubject.setError("Enter subject");

            return;
        }

        // Hide empty text
        tvEmpty.setVisibility(TextView.GONE);

        // CREATE SUBJECT CHIP
        TextView tv = new TextView(this);

        tv.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        tv.setText(subject);

        tv.setTextSize(15f);

        tv.setPadding(24, 18, 24, 18);

        tv.setBackgroundResource(R.drawable.card_blue);

        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams) tv.getLayoutParams();

        params.setMargins(0, 0, 0, 14);

        tv.setLayoutParams(params);

        subjectsContainer.addView(tv);

        etSubject.setText("");
    }
}