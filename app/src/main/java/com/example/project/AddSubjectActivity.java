package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddSubjectActivity extends AppCompatActivity {

    EditText etName, etLevel, etProgress, etHours;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        etName = findViewById(R.id.etName);
        etLevel = findViewById(R.id.etLevel);
        etProgress = findViewById(R.id.etProgress);
        etHours = findViewById(R.id.etHours);
        btnSave = findViewById(R.id.btnSaveSubject);

        btnSave.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.putExtra("name", etName.getText().toString());
            intent.putExtra("level", etLevel.getText().toString());
            intent.putExtra("progress", etProgress.getText().toString());
            intent.putExtra("hours", etHours.getText().toString());

            setResult(RESULT_OK, intent);
            finish();
        });
    }
}