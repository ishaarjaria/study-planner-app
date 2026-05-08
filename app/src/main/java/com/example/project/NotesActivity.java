package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;
public class NotesActivity extends AppCompatActivity {
    private static final String TAG = "NotesActivity";

    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    TextView btnBack;
    EditText etQuickNote;
    Button btnSaveNote;
    LinearLayout notesListContainer;

    private FirebaseFirestore firestore;
    private String uid;
    private ListenerRegistration noteListener;
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
        setContentView(R.layout.activity_notes);

        firestore = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        navDashboard = findViewById(R.id.navDashboard);
        navCalendar = findViewById(R.id.navCalendar);
        navTasks = findViewById(R.id.navTasks);
        navProgress = findViewById(R.id.navProgress);
        navProfile = findViewById(R.id.navProfile);
        btnBack = findViewById(R.id.btnBack);
        etQuickNote = findViewById(R.id.etQuickNote);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        notesListContainer = findViewById(R.id.notesListContainer);
        etQuickNote.setOnClickListener(v -> startActivity(new Intent(this, AddNoteActivity.class)));

        btnSaveNote.setOnClickListener(v -> startActivity(new Intent(this, AddNoteActivity.class)));
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
        btnBack.setOnClickListener(v -> navigateToLanding());

        observeNotes();
    }

    private void observeNotes() {
        noteListener = firestore.collection("users")
                .document(uid)
                .collection("notes")
                .addSnapshotListener((query, e) -> {
                    if (e != null || query == null) {
                        Log.e(TAG, "Notes listener error", e);
                        return;
                    }

                    notesListContainer.removeAllViews();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        addNoteCard(doc.getString("title"), doc.getString("content"), doc.getLong("createdAt"));
                    }
                });
    }

    private void addNoteCard(String title, String content, Long createdAt) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(cardDrawables[random.nextInt(cardDrawables.length)]);
        int pad = dpToPx(16);
        card.setPadding(pad, pad, pad, pad);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(24), 0, dpToPx(24), dpToPx(12));
        card.setLayoutParams(params);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title == null || title.trim().isEmpty() ? "Quick Note" : title);
        tvTitle.setTextSize(15f);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvDate = new TextView(this);
        String dateText = createdAt == null ? "Now"
                : new SimpleDateFormat("dd MMM", Locale.getDefault()).format(createdAt);
        tvDate.setText(dateText);
        tvDate.setTextSize(12f);
        tvDate.setPadding(0, dpToPx(2), 0, 0);

        TextView tvContent = new TextView(this);
        tvContent.setText(content == null ? "Untitled note" : content);
        tvContent.setTextSize(13f);
        tvContent.setPadding(0, dpToPx(8), 0, 0);

        card.addView(tvTitle);
        card.addView(tvDate);
        card.addView(tvContent);
        notesListContainer.addView(card);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
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
        if (noteListener != null) {
            noteListener.remove();
        }
    }
}