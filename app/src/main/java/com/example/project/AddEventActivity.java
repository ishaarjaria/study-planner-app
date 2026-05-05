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

public class AddEventActivity extends AppCompatActivity {
    private static final String TAG = "AddEventActivity";

    private EditText etEventTitle;
    private EditText etEventDate;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDate = findViewById(R.id.etEventDate);
        Button btnSaveEvent = findViewById(R.id.btnSaveEvent);
        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String title = etEventTitle.getText().toString().trim();
        String date = etEventDate.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            etEventTitle.setError("Enter event title");
            return;
        }
        if (TextUtils.isEmpty(date)) {
            etEventDate.setError("Enter event date (dd-MM-yyyy)");
            return;
        }
        long eventDateMillis;
        try {
            eventDateMillis = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(date).getTime();
        } catch (ParseException | NullPointerException e) {
            etEventDate.setError("Invalid date format");
            return;
        }
        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", title);
        eventData.put("date", date);
        eventData.put("eventDateMillis", eventDateMillis);
        eventData.put("createdAt", System.currentTimeMillis());

        firestore.collection("users").document(currentUser.getUid()).collection("events")
                .add(eventData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Event save failed", e);
                    Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show();
                });
    }
}
