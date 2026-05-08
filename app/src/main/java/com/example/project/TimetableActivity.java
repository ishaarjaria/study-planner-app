package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TimetableActivity extends AppCompatActivity {

    private static final String TAG = "TimetableActivity";

    private EditText etSubjects, etDate, etHours, etPriority;
    private FirebaseFirestore firestore;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        firestore = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Views
        etSubjects = findViewById(R.id.etSubjects);
        etDate = findViewById(R.id.etDate);
        etHours = findViewById(R.id.etHours);
        etPriority = findViewById(R.id.etPriority);
        Button btnGenerate = findViewById(R.id.btnGenerate);

        // Load existing data
        firestore.collection("timetables").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        etSubjects.setText(doc.getString("subjects"));
                        etDate.setText(doc.getString("examDate"));
                        etHours.setText(doc.getString("dailyHours"));
                        etPriority.setText(doc.getString("prioritySubject"));
                    }
                });

        // Generate
        btnGenerate.setOnClickListener(v -> {

            String subjects = etSubjects.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String hours = etHours.getText().toString().trim();
            String priority = etPriority.getText().toString().trim();

            if (subjects.isEmpty() || date.isEmpty() || hours.isEmpty() || priority.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String aiPlan = "Plan:\n- Study " + priority +
                    "\n- " + hours + " hrs/day\n- Start: " + date;

            Map<String, Object> data = new HashMap<>();
            data.put("uid", uid);
            data.put("subjects", subjects);
            data.put("examDate", date);
            data.put("dailyHours", hours);
            data.put("prioritySubject", priority);
            data.put("aiPlan", aiPlan);
            data.put("updatedAt", System.currentTimeMillis());

            firestore.collection("timetables").document(uid).set(data)
                    .addOnFailureListener(e -> Log.e(TAG, "Save failed", e));

            Intent intent = new Intent(this, TimetableResultActivity.class);
            intent.putExtra("aiPlan", aiPlan);
            startActivity(intent);
        });

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
}