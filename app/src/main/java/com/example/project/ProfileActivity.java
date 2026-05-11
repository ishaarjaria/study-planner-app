package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    TextView btnBack, btnLogout, btnAccount, btnEdit, tvProfileName, tvProfileEmail;

    private ListenerRegistration profileListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 🔹 INIT VIEWS
        navDashboard = findViewById(R.id.navDashboard);
        navCalendar = findViewById(R.id.navCalendar);
        navTasks = findViewById(R.id.navTasks);
        navProgress = findViewById(R.id.navProgress);
        navProfile = findViewById(R.id.navProfile);

        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        btnAccount = findViewById(R.id.btnAccount);
        btnEdit = findViewById(R.id.btnEdit);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);

        // 🔽 Bottom Navigation
        setupBottomNavigation();

        // 🔙 BACK → Landing
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, LandingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // 🔹 Buttons
        btnAccount.setOnClickListener(v ->
                startActivity(new Intent(this, AccountActivity.class)));

        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(this, SetupProfileActivity.class)));

        // 🔥 LOGOUT
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 🔥 FETCH PROFILE DATA
        observeProfile();
    }

    private void setupBottomNavigation() {
        setNavClick(navDashboard, DashboardActivity.class);
        setNavClick(navCalendar, CalendarActivity.class);
        setNavClick(navTasks, TasksActivity.class);
        setNavClick(navProgress, ProgressActivity.class);
        // Profile pe already ho → no action
    }

    private void setNavClick(LinearLayout navView, Class<?> target) {
        if (navView == null) return;

        navView.setOnClickListener(v ->
                startActivity(new Intent(this, target)));
    }

    private void observeProfile() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        profileListener = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .addSnapshotListener((doc, e) -> {

                    if (e != null || doc == null || !doc.exists()) {
                        Log.e(TAG, "Profile load failed", e);
                        return;
                    }

                    String name = doc.getString("name");
                    String email = doc.getString("email");

                    tvProfileName.setText(
                            name == null || name.isEmpty() ? "Student" : name);

                    tvProfileEmail.setText(
                            email == null || email.isEmpty() ? "No email" : email);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileListener != null) profileListener.remove();
    }
}