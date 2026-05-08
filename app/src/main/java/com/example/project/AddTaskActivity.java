package com.example.project;

import android.app.DatePickerDialog;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddTaskActivity";

    private EditText etTask, etDate;
    private Spinner spPriority;
    private Button btnSave;

    private FirebaseFirestore firestore;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        firestore = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        etTask = findViewById(R.id.etTask);
        etDate = findViewById(R.id.etDate);
        spPriority = findViewById(R.id.spPriority);
        btnSave = findViewById(R.id.btnSave);
        etDate.setOnClickListener(v -> showDatePicker());

        // Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Low", "Medium", "High"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(adapter);

        // Save
        btnSave.setOnClickListener(v -> saveTask());

        // Navigation
        findViewById(R.id.btnBack).setOnClickListener(v -> navigateToLanding());

        findViewById(R.id.navDashboard).setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        findViewById(R.id.navCalendar).setOnClickListener(v ->
                startActivity(new Intent(this, CalendarActivity.class)));

        findViewById(R.id.navTasks).setOnClickListener(v -> finish());

        findViewById(R.id.navProgress).setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));

        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth, 0, 0, 0);
                    String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(selected.getTime());
                    etDate.setText(formattedDate);
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveTask() {

        String task = etTask.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String priority = spPriority.getSelectedItem().toString();

        if (TextUtils.isEmpty(task)) {
            etTask.setError("Enter task");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("title", task);
        data.put("date", date);
        data.put("priority", priority);
        data.put("completed", false);
        data.put("createdAt", System.currentTimeMillis());

        firestore.collection("users")
                .document(uid)
                .collection("tasks")
                .add(data)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Save failed", e);
                    Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}