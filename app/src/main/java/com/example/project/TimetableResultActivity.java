package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TimetableResultActivity extends AppCompatActivity {

    private static final String TAG = "TimetableResultActivity";

    private TextView tvGeneratedSchedule;
    private Button btnRegenerate, btnSave;

    private FirebaseFirestore firestore;
    private String uid;
    private String generatedPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_result);

        firestore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Views
        tvGeneratedSchedule = findViewById(R.id.tvGeneratedSchedule);
        btnRegenerate = findViewById(R.id.btnRegenerate);
        btnSave = findViewById(R.id.btnSave);

        // Get generated plan from previous screen
        generatedPlan = getIntent().getStringExtra("generatedPlan");

        if (generatedPlan != null && !generatedPlan.trim().isEmpty()) {
            tvGeneratedSchedule.setText(generatedPlan);
        } else {
            loadSavedTimetable();
        }

        // Regenerate → go back
        btnRegenerate.setOnClickListener(v -> finish());

        // Save timetable
        btnSave.setOnClickListener(v -> saveTimetable());

        // Navigation
        findViewById(R.id.navDashboard).setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        findViewById(R.id.navCalendar).setOnClickListener(v ->
                startActivity(new Intent(this, CalendarActivity.class)));

        findViewById(R.id.navTasks).setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        findViewById(R.id.navProgress).setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));

        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void loadSavedTimetable() {
        firestore.collection("users")
                .document(uid)
                .collection("timetable")
                .document("latest")
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String plan = doc.getString("generatedPlan");
                        if (plan != null && !plan.isEmpty()) {
                            tvGeneratedSchedule.setText(plan);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Load failed", e));
    }

    private void saveTimetable() {

        String plan = generatedPlan != null
                ? generatedPlan
                : tvGeneratedSchedule.getText().toString();

        Map<String, Object> data = new HashMap<>();
        data.put("generatedPlan", plan);
        data.put("saved", true);
        data.put("savedAt", System.currentTimeMillis());

        firestore.collection("users")
                .document(uid)
                .collection("timetable")
                .document("latest")
                .set(data)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Save failed", e);
                    Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
                });
    }
}