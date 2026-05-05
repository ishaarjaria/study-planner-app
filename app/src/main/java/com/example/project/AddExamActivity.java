package com.example.project;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);
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

        if (TextUtils.isEmpty(title)) {
            etExamTitle.setError("Enter exam title");
            return;
        }
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
    }
}
