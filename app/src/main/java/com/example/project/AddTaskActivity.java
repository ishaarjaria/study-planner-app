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
<<<<<<< HEAD
import com.google.firebase.auth.FirebaseUser;
=======
>>>>>>> 58c259c (new changes)
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {
    private static final String TAG = "AddTaskActivity";

    EditText etTask;
    EditText etDate;
    Spinner spPriority;
    Button btnSave;
<<<<<<< HEAD
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
=======
    FirebaseFirestore firestore;
    private static final String TAG = "AddTaskActivity";
>>>>>>> 58c259c (new changes)

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
<<<<<<< HEAD
=======
        firestore = FirebaseFirestore.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Low", "Medium", "High"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(adapter);

>>>>>>> 58c259c (new changes)
        TextView btnBack = findViewById(R.id.btnBack);
        LinearLayout navDashboard = findViewById(R.id.navDashboard);
        LinearLayout navCalendar = findViewById(R.id.navCalendar);
        LinearLayout navTasks = findViewById(R.id.navTasks);
        LinearLayout navProgress = findViewById(R.id.navProgress);
        LinearLayout navProfile = findViewById(R.id.navProfile);

<<<<<<< HEAD
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
=======
        btnBack.setOnClickListener(v -> finish());
        navDashboard.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        navCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        navTasks.setOnClickListener(v -> startActivity(new Intent(this, TasksActivity.class)));
        navProgress.setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        btnSave.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            String task = etTask.getText().toString().trim();
            String dueDate = etDate.getText().toString().trim();
            String priority = spPriority.getSelectedItem() == null ? "Medium" : spPriority.getSelectedItem().toString();

            if (TextUtils.isEmpty(task)) {
                etTask.setError("Enter task");
                return;
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            payload.put("title", task);
            payload.put("dueDate", dueDate);
            payload.put("priority", priority);
            payload.put("completed", false);
            payload.put("createdAt", System.currentTimeMillis());

            firestore.collection("tasks").add(payload).addOnSuccessListener(ref -> {
                Intent intent = new Intent();
                intent.putExtra("task", task);
                intent.putExtra("taskId", ref.getId());
                intent.putExtra("priority", priority);
                intent.putExtra("dueDate", dueDate);
                setResult(RESULT_OK, intent);
                finish();
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to save task", e);
                Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show();
            });

>>>>>>> 58c259c (new changes)
        });

        btnBack.setOnClickListener(v -> finish());
        navDashboard.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        navCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        navTasks.setOnClickListener(v -> finish());
        navProgress.setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }
}