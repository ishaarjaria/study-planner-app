package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";

    private CalendarView calendarView;
    private LinearLayout eventListContainer;
    private TextView btnBack;

    private FirebaseFirestore firestore;
    private String uid;
    private ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        firestore = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        calendarView = findViewById(R.id.calendarView);
        eventListContainer = findViewById(R.id.eventListContainer);
        Button btnAddEvent = findViewById(R.id.btnAddEvent);
        btnBack = findViewById(R.id.btnBack);

        // Fix 1970 bug
        calendarView.setDate(System.currentTimeMillis(), true, true);

        calendarView.setOnDateChangeListener((view, y, m, d) ->
                Toast.makeText(this,
                        "Selected: " + d + "/" + (m + 1) + "/" + y,
                        Toast.LENGTH_SHORT).show());

        btnAddEvent.setOnClickListener(v ->
                startActivity(new Intent(this, AddEventActivity.class)));
        btnBack.setOnClickListener(v -> navigateToLanding());

        setupBottomNavigation();

        observeEvents();
    }

    private void setupBottomNavigation() {
        setNavClick(R.id.navDashboard, DashboardActivity.class, false);
        setNavClick(R.id.navCalendar, CalendarActivity.class, true);
        setNavClick(R.id.navTasks, TasksActivity.class, false);
        setNavClick(R.id.navProgress, ProgressActivity.class, false);
        setNavClick(R.id.navProfile, ProfileActivity.class, false);
    }

    private void setNavClick(int viewId, Class<?> target, boolean isCurrentPage) {
        android.view.View nav = findViewById(viewId);
        if (nav == null) {
            Log.e(TAG, "Missing nav view id: " + viewId);
            return;
        }
        nav.setClickable(true);
        nav.setFocusable(true);
        nav.setOnClickListener(v -> {
            if (!isCurrentPage) {
                startActivity(new Intent(this, target));
            }
        });
    }

    private void observeEvents() {
        listener = firestore.collection("users")
                .document(uid)
                .collection("events")
                .addSnapshotListener((query, e) -> {

                    if (e != null || query == null) {
                        Log.e(TAG, "Error loading events", e);
                        return;
                    }

                    eventListContainer.removeAllViews();

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        String title = doc.getString("title");
                        Long date = doc.getLong("dateMillis");

                        addEventCard(
                                title == null ? "Untitled Event" : title,
                                date
                        );
                    }
                });
    }

    private void addEventCard(String title, Long dateMillis) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        int cardPadding = dpToPx(16);
        card.setPadding(cardPadding, cardPadding, cardPadding, cardPadding);
        card.setBackgroundResource(R.drawable.card_blue);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(24), dpToPx(14), dpToPx(24), 0);
        card.setLayoutParams(params);
        card.setGravity(android.view.Gravity.CENTER_VERTICAL);

        String dateText = dateMillis == null
                ? "No date"
                : new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(dateMillis);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextSize(15f);
        tvTitle.setTextAlignment(TextView.TEXT_ALIGNMENT_VIEW_START);

        TextView tvDate = new TextView(this);
        tvDate.setText(dateText);
        tvDate.setTextSize(13f);
        tvDate.setPadding(0, dpToPx(6), 0, 0);
        tvDate.setTextAlignment(TextView.TEXT_ALIGNMENT_VIEW_START);

        card.addView(tvTitle);
        card.addView(tvDate);
        eventListContainer.addView(card);
    }

    private void navigateToLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }
}