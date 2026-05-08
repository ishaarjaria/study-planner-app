package com.example.project;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = "AddEventActivity";

    private EditText etEventTitle, etEventDate;
    private long selectedDateMillis = 0L;

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

        Button btnSave = findViewById(R.id.btnSaveEvent);
        Button btnCancel = findViewById(R.id.btnCancelEvent);

        etEventDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveEvent());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth, 0, 0, 0);

                    selectedDateMillis = selected.getTimeInMillis();

                    String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            .format(selected.getTime());

                    etEventDate.setText(formattedDate);
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void saveEvent() {

        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etEventTitle.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etEventTitle.setError("Enter event title");
            return;
        }

        if (selectedDateMillis == 0L) {
            etEventDate.setError("Select event date");
            return;
        }

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", title);
        eventData.put("dateMillis", selectedDateMillis);
        eventData.put("createdAt", System.currentTimeMillis());

        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("events")
                .add(eventData)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding event", e);
                    Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show();
                });
    }
}