package com.example.project;

<<<<<<< HEAD
=======
import android.app.DatePickerDialog;
>>>>>>> 58c259c (new changes)
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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

public class AddEventActivity extends AppCompatActivity {
    private static final String TAG = "AddEventActivity";

    private EditText etEventTitle;
    private EditText etEventDate;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
=======
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {
    private EditText etEventTitle;
    private EditText etEventDate;
    private long selectedDateMillis = 0L;
    private FirebaseFirestore firestore;
    private static final String TAG = "AddEventActivity";
>>>>>>> 58c259c (new changes)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
<<<<<<< HEAD
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
=======

        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDate = findViewById(R.id.etEventDate);
        Button btnSave = findViewById(R.id.btnSaveEvent);
        Button btnCancel = findViewById(R.id.btnCancelEvent);
        firestore = FirebaseFirestore.getInstance();

        etEventDate.setOnClickListener(v -> showDatePicker());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveEvent());
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth, 0, 0, 0);
            selectedDateMillis = selected.getTimeInMillis();
            etEventDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveEvent() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String title = etEventTitle.getText().toString().trim();
>>>>>>> 58c259c (new changes)
        if (TextUtils.isEmpty(title)) {
            etEventTitle.setError("Enter event title");
            return;
        }
<<<<<<< HEAD
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
=======
        if (selectedDateMillis == 0L) {
            etEventDate.setError("Select event date");
            return;
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        payload.put("title", title);
        payload.put("dateMillis", selectedDateMillis);
        payload.put("createdAt", System.currentTimeMillis());

        firestore.collection("events").add(payload).addOnSuccessListener(ref -> {
            Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to save event", e);
            Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show();
        });
>>>>>>> 58c259c (new changes)
    }
}
