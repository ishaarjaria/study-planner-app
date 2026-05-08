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

public class AddReminderActivity extends AppCompatActivity {
    private static final String TAG = "AddReminderActivity";

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

        btnSave.setOnClickListener(v -> saveReminder());
    }

    private void saveReminder() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = etTitle.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String time = etTime.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Enter title");
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("title", title);
        payload.put("subject", subject);
        payload.put("time", time);
        payload.put("completed", false);
        payload.put("createdAt", System.currentTimeMillis());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .collection("reminders")
                .add(payload)
                .addOnSuccessListener(r -> {
                    Intent intent = new Intent();
                    intent.putExtra("title", title);
                    intent.putExtra("subject", subject);
                    intent.putExtra("time", time);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Reminder save failed", e);
                    Toast.makeText(this, "Failed to save reminder", Toast.LENGTH_SHORT).show();
                });
    }
}