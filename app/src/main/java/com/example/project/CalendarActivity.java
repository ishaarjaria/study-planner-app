package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    private static final String TAG = "CalendarActivity";

    CalendarView calendarView;
    TextView btnBack;
    Button btnAddEvent;
    LinearLayout eventListContainer;

    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // 🔗 Link Views
        calendarView = findViewById(R.id.calendarView);
        btnBack = findViewById(R.id.btnBack);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        eventListContainer = findViewById(R.id.eventListContainer);

        navDashboard = findViewById(R.id.navDashboard);
        navCalendar = findViewById(R.id.navCalendar);
        navTasks = findViewById(R.id.navTasks);
        navProgress = findViewById(R.id.navProgress);
        navProfile = findViewById(R.id.navProfile);

        // ✅ FIX: Set current date (solves 1970 problem)
        calendarView.setDate(System.currentTimeMillis(), true, true);

        // 📅 Date click listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Toast.makeText(this,
                    "Selected: " + dayOfMonth + "/" + (month + 1) + "/" + year,
                    Toast.LENGTH_SHORT).show();
        });

        // 🔙 Back button
        btnBack.setOnClickListener(v -> finish());
        btnAddEvent.setOnClickListener(v -> startActivity(new Intent(this, AddEventActivity.class)));

        // 🔽 Bottom Navigation

        navDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        navCalendar.setOnClickListener(v ->
                Toast.makeText(this, "Already on Calendar", Toast.LENGTH_SHORT).show());

        navTasks.setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        navProgress.setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchEvents();
    }

    private void fetchEvents() {
        if (currentUser == null) {
            Log.e(TAG, "User missing while fetching events");
            return;
        }
        firestore.collection("users").document(currentUser.getUid()).collection("events")
                .orderBy("eventDateMillis")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventListContainer.removeAllViews();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String title = doc.getString("title");
                        String date = doc.getString("date");
                        Long eventDateMillis = doc.getLong("eventDateMillis");
                        addEventCard(title == null ? "Untitled event" : title,
                                date == null && eventDateMillis != null ? formatDate(eventDateMillis) : date);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Event fetch failed", e));
    }

    private void addEventCard(String title, String date) {
        LinearLayout card = new LinearLayout(this);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) card.getLayoutParams()).setMargins(0, 10, 0, 0);
        card.setPadding(16, 16, 16, 16);
        card.setBackgroundResource(R.drawable.card_blue);

        TextView textView = new TextView(this);
        textView.setText(title + "\n" + (date == null ? "" : date));
        card.addView(textView);
        eventListContainer.addView(card);
    }

    private String formatDate(long millis) {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(millis));
    }
}