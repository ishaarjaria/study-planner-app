package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SetupProfile2Activity extends AppCompatActivity {

    // Views
    EditText etDate;

    Button btnBack, btnNext;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile2);

        // 🔹 Initialize Views
        etDate = findViewById(R.id.etDate);

        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);

        // 🔙 BACK BUTTON
        btnBack.setOnClickListener(v -> navigateToLanding());

        // ➡ NEXT BUTTON
        btnNext.setOnClickListener(v -> {

            String date = etDate.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(date)) {

                etDate.setError("Enter exam date");

                etDate.requestFocus();

                return;
            }

            Toast.makeText(
                    SetupProfile2Activity.this,
                    "Exam Date Saved",
                    Toast.LENGTH_SHORT
            ).show();

            // GO TO NEXT SCREEN
            Intent intent = new Intent(
                    SetupProfile2Activity.this,
                    SetupProfile3Activity.class
            );

            startActivity(intent);
        });
    }

    private void navigateToLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}