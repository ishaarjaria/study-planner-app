package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddTaskActivity extends AppCompatActivity {

    EditText etTask;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etTask = findViewById(R.id.etTask);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String task = etTask.getText().toString();

            Intent intent = new Intent();
            intent.putExtra("task", task);

            setResult(RESULT_OK, intent);
            finish();
        });
    }
}