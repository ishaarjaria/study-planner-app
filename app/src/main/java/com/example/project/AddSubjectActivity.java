package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddSubjectActivity extends AppCompatActivity {
    private static final String TAG = "AddSubjectActivity";

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

        btnSave.setOnClickListener(v -> saveSubject());
    }

    private void saveSubject() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = etName.getText().toString().trim();
        String level = etLevel.getText().toString().trim();
        String progress = etProgress.getText().toString().trim();
        String hours = etHours.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            etName.setError("Enter subject name");
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        payload.put("level", level);
        payload.put("progress", progress);
        payload.put("hours", hours);
        payload.put("createdAt", System.currentTimeMillis());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .collection("subjects")
                .add(payload)
                .addOnSuccessListener(r -> {
                    Intent intent = new Intent();
                    intent.putExtra("name", name);
                    intent.putExtra("level", level);
                    intent.putExtra("progress", progress);
                    intent.putExtra("hours", hours);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Subject save failed", e);
                    Toast.makeText(this, "Failed to save subject", Toast.LENGTH_SHORT).show();
                });
    }
}