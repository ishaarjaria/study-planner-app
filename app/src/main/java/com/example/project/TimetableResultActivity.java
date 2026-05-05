package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
<<<<<<< HEAD
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

=======
import com.google.firebase.firestore.FirebaseFirestore;

>>>>>>> 58c259c (new changes)
public class TimetableResultActivity extends AppCompatActivity {
    private static final String TAG = "TimetableResultActivity";

    TextView btnBack, navDashboard, navCalendar, navTasks, navProgress, navProfile;
    Button btnRegenerate, btnSave;
<<<<<<< HEAD
    private TextView tvGeneratedSchedule;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private String generatedPlan;
=======
    TextView tvAiPlan;
    private FirebaseFirestore firestore;
    private String aiPlan;
    private static final String TAG = "TimetableResultActivity";
>>>>>>> 58c259c (new changes)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_result);
        firestore = FirebaseFirestore.getInstance();
<<<<<<< HEAD
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
=======
        aiPlan = getIntent().getStringExtra("aiPlan");
>>>>>>> 58c259c (new changes)

        // 🔗 Link Views
        btnBack = findViewById(R.id.btnBack);

        LinearLayout navDashboard = findViewById(R.id.navDashboard);
        LinearLayout navCalendar = findViewById(R.id.navCalendar);
        LinearLayout navTasks = findViewById(R.id.navTasks);
        LinearLayout navProgress = findViewById(R.id.navProgress);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        btnRegenerate = findViewById(R.id.btnRegenerate);
        btnSave = findViewById(R.id.btnSave);
<<<<<<< HEAD
        tvGeneratedSchedule = findViewById(R.id.tvGeneratedSchedule);
        generatedPlan = getIntent().getStringExtra("generatedPlan");
        if (generatedPlan != null && !generatedPlan.trim().isEmpty()) {
            tvGeneratedSchedule.setText(generatedPlan);
        } else {
            loadSavedTimetable();
=======
        tvAiPlan = findViewById(R.id.tvAiPlan);
        if (aiPlan != null) {
            tvAiPlan.setText(aiPlan);
>>>>>>> 58c259c (new changes)
        }

        // 🔙 Back to previous screen
        btnBack.setOnClickListener(v -> finish());

        // 🔁 Regenerate → go back to input page
        btnRegenerate.setOnClickListener(v -> {
            finish(); // returns to TimetableActivity
        });

        // 💾 Save Schedule
        btnSave.setOnClickListener(v -> {
<<<<<<< HEAD
            saveFinalTimetable();
=======
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
                return;
            }
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firestore.collection("timetables").document(uid)
                    .update("saved", true, "savedAt", System.currentTimeMillis())
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Schedule Saved!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Save schedule failed", e);
                        Toast.makeText(this, "Could not save schedule", Toast.LENGTH_SHORT).show();
                    });
>>>>>>> 58c259c (new changes)
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firestore.collection("timetables").document(uid).get().addOnSuccessListener(doc -> {
                String latestPlan = doc.getString("aiPlan");
                if (latestPlan != null && !latestPlan.trim().isEmpty()) {
                    aiPlan = latestPlan;
                    tvAiPlan.setText(aiPlan);
                }
                if (aiPlan != null) {
                    Toast.makeText(this, "Timetable loaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch timetable", e));
        }

        // 🔽 Bottom Navigation
        navDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        navCalendar.setOnClickListener(v ->
                startActivity(new Intent(this, CalendarActivity.class)));

        navTasks.setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        navProgress.setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void loadSavedTimetable() {
        if (currentUser == null) {
            return;
        }
        firestore.collection("users").document(currentUser.getUid())
                .collection("timetable").document("latest")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String savedPlan = snapshot.getString("generatedPlan");
                    if (savedPlan != null && !savedPlan.trim().isEmpty()) {
                        generatedPlan = savedPlan;
                        tvGeneratedSchedule.setText(savedPlan);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to load timetable", e));
    }

    private void saveFinalTimetable() {
        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> saveData = new HashMap<>();
        saveData.put("generatedPlan", generatedPlan == null ? tvGeneratedSchedule.getText().toString() : generatedPlan);
        saveData.put("saved", true);
        saveData.put("savedAt", System.currentTimeMillis());

        firestore.collection("users").document(currentUser.getUid())
                .collection("timetable")
                .document("latest")
                .set(saveData, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(unused -> Toast.makeText(this, "Schedule Saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Save schedule failed", e);
                    Toast.makeText(this, "Failed to save schedule", Toast.LENGTH_SHORT).show();
                });
    }
}