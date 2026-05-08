package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Random;

public class SuggestionsActivity extends AppCompatActivity {
    private static final String TAG = "SuggestionsActivity";

    private LinearLayout suggestionsListContainer;
    private ListenerRegistration suggestionListener;
    private final Random random = new Random();
    private final int[] cardDrawables = {
            R.drawable.card_green,
            R.drawable.card_blue,
            R.drawable.card_yellow,
            R.drawable.card_purple,
            R.drawable.card_pink,
            R.drawable.card_red
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        suggestionsListContainer = findViewById(R.id.suggestionsListContainer);
        findViewById(R.id.btnBack).setOnClickListener(v -> navigateToLanding());

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

        observeSuggestions();
    }

    private void observeSuggestions() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        suggestionListener = FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .collection("notes")
                .addSnapshotListener((query, e) -> {
                    if (e != null || query == null) {
                        Log.e(TAG, "Suggestions fetch failed", e);
                        return;
                    }
                    suggestionsListContainer.removeAllViews();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String text = doc.getString("content");
                        if (text != null && !text.trim().isEmpty()) {
                            addSuggestionCard("Review this note soon", text);
                        }
                    }
                });
    }

    private void addSuggestionCard(String title, String detail) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(cardDrawables[random.nextInt(cardDrawables.length)]);
        card.setPadding(20, 20, 20, 20);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 12);
        card.setLayoutParams(params);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(14f);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvDetail = new TextView(this);
        tvDetail.setText(detail);
        tvDetail.setTextSize(12f);
        tvDetail.setPadding(0, 6, 0, 0);

        card.addView(tvTitle);
        card.addView(tvDetail);
        suggestionsListContainer.addView(card);
    }

    private void navigateToLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (suggestionListener != null) {
            suggestionListener.remove();
        }
    }
}