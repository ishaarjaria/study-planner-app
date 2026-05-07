package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SetupProfile3Activity extends AppCompatActivity {

    EditText etHours;

    Button btnBack, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile3);

        // INIT
        etHours = findViewById(R.id.etHours);

        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);

        // BACK
        btnBack.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SetupProfile3Activity.this,
                    SetupProfile2Activity.class
            );

            startActivity(intent);

            finish();
        });

        // NEXT
        btnNext.setOnClickListener(v -> {

            String hours = etHours.getText().toString().trim();

            if (TextUtils.isEmpty(hours)) {

                etHours.setError("Enter study hours");

                return;
            }

            Toast.makeText(
                    this,
                    "Study Hours Saved",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    SetupProfile3Activity.this,
                    SetupProfile4Activity.class
            );

            startActivity(intent);
        });
    }
}