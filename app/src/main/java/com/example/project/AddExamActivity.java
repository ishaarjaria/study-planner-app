package com.example.project;

<<<<<<< HEAD
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
=======
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
>>>>>>> 58c259c (new changes)
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
<<<<<<< HEAD
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddExamActivity extends AppCompatActivity {
    private static final String TAG = "AddExamActivity";
    private EditText etExamTitle;
    private EditText etExamDate;
    private EditText etExamProgress;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
=======
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddExamActivity extends AppCompatActivity {
    private EditText etExamTitle;
    private EditText etExamDate;
    private Spinner spProgress;
    private FirebaseFirestore firestore;
    private long selectedDateMillis = 0L;
    private static final String TAG = "AddExamActivity";
>>>>>>> 58c259c (new changes)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);
<<<<<<< HEAD
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        etExamTitle = findViewById(R.id.etExamTitle);
        etExamDate = findViewById(R.id.etExamDate);
        etExamProgress = findViewById(R.id.etExamProgress);
        Button btnSaveExam = findViewById(R.id.btnSaveExam);
        btnSaveExam.setOnClickListener(v -> saveExam());
    }

    private void saveExam() {
        String title = etExamTitle.getText().toString().trim();
        String date = etExamDate.getText().toString().trim();
        String progressText = etExamProgress.getText().toString().trim();

=======

        etExamTitle = findViewById(R.id.etExamTitle);
        etExamDate = findViewById(R.id.etExamDate);
        spProgress = findViewById(R.id.spExamProgress);
        Button btnSave = findViewById(R.id.btnSaveExam);
        Button btnCancel = findViewById(R.id.btnCancelExam);
        firestore = FirebaseFirestore.getInstance();

        ArrayAdapter<String> progressAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"0", "25", "50", "75", "100"});
        progressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProgress.setAdapter(progressAdapter);

        etExamDate.setOnClickListener(v -> showDatePicker());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveExam());
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth, 0, 0, 0);
            selectedDateMillis = selected.getTimeInMillis();
            etExamDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveExam() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String title = etExamTitle.getText().toString().trim();
>>>>>>> 58c259c (new changes)
        if (TextUtils.isEmpty(title)) {
            etExamTitle.setError("Enter exam title");
            return;
        }
<<<<<<< HEAD
        if (TextUtils.isEmpty(date)) {
            etExamDate.setError("Enter exam date (dd-MM-yyyy)");
            return;
        }
        long examDateMillis;
        try {
            examDateMillis = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(date).getTime();
        } catch (ParseException | NullPointerException e) {
            etExamDate.setError("Invalid date format");
            return;
        }
        int progress = 0;
        if (!TextUtils.isEmpty(progressText)) {
            try {
                progress = Integer.parseInt(progressText);
            } catch (NumberFormatException ignored) {
                progress = 0;
            }
        }
        progress = Math.max(0, Math.min(progress, 100));
        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> examData = new HashMap<>();
        examData.put("title", title);
        examData.put("date", date);
        examData.put("examDateMillis", examDateMillis);
        examData.put("progress", progress);
        examData.put("createdAt", System.currentTimeMillis());

        firestore.collection("users").document(currentUser.getUid()).collection("exams")
                .add(examData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Exam added", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Exam save failed", e);
                    Toast.makeText(this, "Failed to save exam", Toast.LENGTH_SHORT).show();
                });
=======
        if (selectedDateMillis == 0L) {
            etExamDate.setError("Select exam date");
            return;
        }

        int progress = Integer.parseInt(spProgress.getSelectedItem().toString());
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        payload.put("title", title);
        payload.put("dateMillis", selectedDateMillis);
        payload.put("progress", progress);
        payload.put("createdAt", System.currentTimeMillis());

        firestore.collection("exams").add(payload).addOnSuccessListener(ref -> {
            Toast.makeText(this, "Exam added", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to save exam", e);
            Toast.makeText(this, "Failed to add exam", Toast.LENGTH_SHORT).show();
        });
>>>>>>> 58c259c (new changes)
    }
}
