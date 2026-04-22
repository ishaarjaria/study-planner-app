package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddReminderActivity extends AppCompatActivity {

    EditText etTitle, etSubject, etTime;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        etTitle = findViewById(R.id.etTitle);
        etSubject = findViewById(R.id.etSubject);
        etTime = findViewById(R.id.etTime);
        btnSave = findViewById(R.id.btnSaveReminder);

        btnSave.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.putExtra("title", etTitle.getText().toString());
            intent.putExtra("subject", etSubject.getText().toString());
            intent.putExtra("time", etTime.getText().toString());

            setResult(RESULT_OK, intent);
            finish();
        });
    }
}