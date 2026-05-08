package com.example.project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddExamActivity extends AppCompatActivity {

    private static final String TAG = "AddExamActivity";

    private EditText etExamTitle, etExamDate;
    private Spinner spProgress;
    private long selectedDateMillis = 0L;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);

        firestore = FirebaseFirestore.getInstance();

        etExamTitle = findViewById(R.id.etExamTitle);
        etExamDate = findViewById(R.id.etExamDate);
        spProgress = findViewById(R.id.spExamProgress);

        Button btnSave = findViewById(R.id.btnSaveExam);
        Button btnCancel = findViewById(R.id.btnCancelExam);

        // Spinner setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"0", "25", "50", "75", "100"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProgress.setAdapter(adapter);

        etExamDate.setOnClickListener(v -> showDatePicker());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveExam());
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth, 0, 0, 0);

                    selectedDateMillis = selected.getTimeInMillis();
                    etExamDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void saveExam() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etExamTitle.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etExamTitle.setError("Enter exam title");
            return;
        }

        if (selectedDateMillis == 0L) {
            etExamDate.setError("Select exam date");
            return;
        }

        int progress = Integer.parseInt(spProgress.getSelectedItem().toString());

        Map<String, Object> examData = new HashMap<>();
        examData.put("title", title);
        examData.put("dateMillis", selectedDateMillis);
        examData.put("progress", progress);
        examData.put("createdAt", System.currentTimeMillis());

        firestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("exams")
                .add(examData)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Exam added", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding exam", e);
                    Toast.makeText(this, "Failed to add exam", Toast.LENGTH_SHORT).show();
                });
    }
}