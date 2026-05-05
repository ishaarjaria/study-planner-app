package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {
    private static final String TAG = "AddTaskActivity";

    EditText etTask;
    EditText etDate;
    Spinner spPriority;
    Button btnSave;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        etTask = findViewById(R.id.etTask);
        etDate = findViewById(R.id.etDate);
        spPriority = findViewById(R.id.spPriority);
        btnSave = findViewById(R.id.btnSave);
        TextView btnBack = findViewById(R.id.btnBack);
        LinearLayout navDashboard = findViewById(R.id.navDashboard);
        LinearLayout navCalendar = findViewById(R.id.navCalendar);
        LinearLayout navTasks = findViewById(R.id.navTasks);
        LinearLayout navProgress = findViewById(R.id.navProgress);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"High", "Medium", "Low"});
        spPriority.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            String task = etTask.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String priority = spPriority.getSelectedItem() == null ? "Medium" : spPriority.getSelectedItem().toString();

            if (TextUtils.isEmpty(task)) {
                etTask.setError("Task is required");
                return;
            }
            if (currentUser == null) {
                Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> taskData = new HashMap<>();
            taskData.put("title", task);
            taskData.put("date", date);
            taskData.put("priority", priority);
            taskData.put("completed", false);
            taskData.put("createdAt", System.currentTimeMillis());

            firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("tasks")
                    .add(taskData)
                    .addOnSuccessListener(documentReference -> {
                        Intent intent = new Intent();
                        intent.putExtra("task", task);
                        setResult(RESULT_OK, intent);
                        Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Task save failed", e);
                        Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show();
                    });
        });

        btnBack.setOnClickListener(v -> finish());
        navDashboard.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        navCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        navTasks.setOnClickListener(v -> finish());
        navProgress.setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }
}