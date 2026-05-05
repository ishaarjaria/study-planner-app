package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TimetableActivity extends AppCompatActivity {
    private static final String TAG = "TimetableActivity";

    EditText etSubjects, etDate, etHours, etPriority;
    Button btnGenerate;
    TextView btnBack, navDashboard, navCalendar, navTasks, navProgress, navProfile;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // 🔗 Link UI
        etSubjects = findViewById(R.id.etSubjects);
        etDate = findViewById(R.id.etDate);
        etHours = findViewById(R.id.etHours);
        etPriority = findViewById(R.id.etPriority);

        btnGenerate = findViewById(R.id.btnGenerate);
        btnBack = findViewById(R.id.btnBack);

        LinearLayout navDashboard = findViewById(R.id.navDashboard);
        LinearLayout navCalendar = findViewById(R.id.navCalendar);
        LinearLayout navTasks = findViewById(R.id.navTasks);
        LinearLayout navProgress = findViewById(R.id.navProgress);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        // 🔙 Back button
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // 🚀 Generate Button
        btnGenerate.setOnClickListener(v -> {

            String subjects = etSubjects.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String hours = etHours.getText().toString().trim();
            String priority = etPriority.getText().toString().trim();

            if (subjects.isEmpty() || date.isEmpty() || hours.isEmpty() || priority.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                String generatedPlan = "Subjects: " + subjects
                        + "\nExam Start: " + date
                        + "\nDaily Hours: " + hours
                        + "\nPriority: " + priority
                        + "\n\nAI Plan:\n- Focus " + priority + " first each day\n- Split revision + practice sessions\n- Keep 30 min recap nightly";

                saveTimetableDraft(subjects, date, hours, priority, generatedPlan);
                Intent intent = new Intent(this, TimetableResultActivity.class);
                intent.putExtra("generatedPlan", generatedPlan);
                startActivity(intent);
            }
        });
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

    private void saveTimetableDraft(String subjects, String date, String hours, String priority, String generatedPlan) {
        if (currentUser == null) {
            Log.e(TAG, "Cannot save timetable, user null");
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("subjects", subjects);
        data.put("examStartDate", date);
        data.put("dailyHours", hours);
        data.put("prioritySubject", priority);
        data.put("generatedPlan", generatedPlan);
        data.put("updatedAt", System.currentTimeMillis());

        firestore.collection("users").document(currentUser.getUid())
                .collection("timetable")
                .document("latest")
                .set(data)
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save timetable draft", e));
    }
}