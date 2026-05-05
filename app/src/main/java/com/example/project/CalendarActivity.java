package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
<<<<<<< HEAD
import android.view.ViewGroup;
=======
>>>>>>> 58c259c (new changes)
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
<<<<<<< HEAD
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
=======
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.List;
>>>>>>> 58c259c (new changes)
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    private static final String TAG = "CalendarActivity";

    CalendarView calendarView;
    TextView btnBack;
    Button btnAddEvent;
    LinearLayout eventListContainer;
<<<<<<< HEAD
=======
    FirebaseFirestore firestore;
    String uid;
    ListenerRegistration eventsListener;
    private static final String TAG = "CalendarActivity";
>>>>>>> 58c259c (new changes)

    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        firestore = FirebaseFirestore.getInstance();
<<<<<<< HEAD
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
=======
        uid = FirebaseAuth.getInstance().getCurrentUser() == null
                ? null : FirebaseAuth.getInstance().getCurrentUser().getUid();
>>>>>>> 58c259c (new changes)

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

        if (uid == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (uid != null && eventsListener == null) {
            observeEventsRealtime();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventsListener != null) {
            eventsListener.remove();
            eventsListener = null;
        }
    }

    private void loadEvents() {
        firestore.collection("events")
                .whereEqualTo("uid", uid)
                .orderBy("dateMillis")
                .get()
                .addOnSuccessListener(query -> {
                    eventListContainer.removeAllViews();
                    List<DocumentSnapshot> docs = query.getDocuments();
                    for (DocumentSnapshot doc : docs) {
                        String title = doc.getString("title");
                        Long date = doc.getLong("dateMillis");
                        addEventCard(title == null ? "Untitled Event" : title, date);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to load events", e));
    }

    private void observeEventsRealtime() {
        eventsListener = firestore.collection("events")
                .whereEqualTo("uid", uid)
                .orderBy("dateMillis")
                .addSnapshotListener((query, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Realtime events listener failed", e);
                        return;
                    }
                    if (query == null) {
                        Log.e(TAG, "Realtime events query null");
                        return;
                    }
                    eventListContainer.removeAllViews();
                    List<DocumentSnapshot> docs = query.getDocuments();
                    for (DocumentSnapshot doc : docs) {
                        String title = doc.getString("title");
                        Long date = doc.getLong("dateMillis");
                        addEventCard(title == null ? "Untitled Event" : title, date);
                    }
                    Log.d(TAG, "Events updated in UI, count=" + docs.size());
                });
    }

    private void addEventCard(String title, Long dateMillis) {
        LinearLayout card = new LinearLayout(this);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        card.setPadding(16, 16, 16, 16);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) card.getLayoutParams();
        lp.setMargins(0, 10, 0, 0);
        card.setLayoutParams(lp);
        card.setBackgroundResource(R.drawable.card_blue);

        TextView tv = new TextView(this);
        String dateText = dateMillis == null ? "Date not set"
                : new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(dateMillis);
        tv.setText(title + "\n" + dateText);
        card.addView(tv);
        eventListContainer.addView(card);
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