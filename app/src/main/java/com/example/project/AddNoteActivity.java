package com.example.project;

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

public class AddNoteActivity extends AppCompatActivity {
    private static final String TAG = "AddNoteActivity";

    private EditText etNoteTitle;
    private EditText etNoteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        Button btnCancel = findViewById(R.id.btnCancelNote);
        Button btnSave = findViewById(R.id.btnSaveNote);

        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            etNoteContent.setError("Enter note content");
            return;
        }
        if (TextUtils.isEmpty(title)) {
            title = "Quick Note";
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("title", title);
        payload.put("content", content);
        payload.put("createdAt", System.currentTimeMillis());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .collection("notes")
                .add(payload)
                .addOnSuccessListener(r -> {
                    Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Note save failed", e);
                    Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show();
                });
    }
}
