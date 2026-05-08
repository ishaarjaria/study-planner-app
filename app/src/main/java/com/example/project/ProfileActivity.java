package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ProfileActivity extends AppCompatActivity {

    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    TextView btnBack, btnLogout, btnAccount, btnNotifications, btnEdit, btnSupport, tvProfileName, tvProfileEmail;
    Switch darkModeSwitch;

    SharedPreferences prefs;
    private static final String TAG = "ProfileActivity";
    private ListenerRegistration profileListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 LOAD SAVED THEME FIRST
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("darkMode", false);

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

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
        btnNotifications = findViewById(R.id.btnNotifications);
        btnEdit = findViewById(R.id.btnEdit);
        btnSupport = findViewById(R.id.btnSupport);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);

        darkModeSwitch = findViewById(R.id.switchDarkMode);

        // 🔥 SET SWITCH STATE
        darkModeSwitch.setChecked(isDark);

        // 🔥 DARK MODE TOGGLE
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("darkMode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            Log.d(TAG, "Dark mode toggled: " + isChecked);
        });

        setupBottomNavigation();

        // 🔹 BACK
        btnBack.setOnClickListener(v -> navigateToLanding());

        // 🔹 SETTINGS BUTTONS (you can create these pages later)
        btnAccount.setOnClickListener(v ->
                startActivity(new Intent(this, AccountActivity.class)));

        btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationActivity.class)));

        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(this, SetupProfileActivity.class)));

        btnSupport.setOnClickListener(v ->
                startActivity(new Intent(this, SupportActivity.class)));

        // 🔥 LOGOUT
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        observeProfile();
    }

    private void setupBottomNavigation() {
        setNavClick(navDashboard, DashboardActivity.class, false);
        setNavClick(navCalendar, CalendarActivity.class, false);
        setNavClick(navTasks, TasksActivity.class, false);
        setNavClick(navProgress, ProgressActivity.class, false);
        setNavClick(navProfile, ProfileActivity.class, true);
    }

    private void setNavClick(LinearLayout navView, Class<?> target, boolean isCurrentPage) {
        if (navView == null) {
            Log.e(TAG, "Navigation view is missing for " + target.getSimpleName());
            return;
        }
        navView.setClickable(true);
        navView.setFocusable(true);
        navView.setOnClickListener(v -> {
            if (!isCurrentPage) {
                startActivity(new Intent(this, target));
            }
        });
    }

    private void navigateToLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void observeProfile() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        profileListener = FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .addSnapshotListener((doc, e) -> {
                    if (e != null || doc == null || !doc.exists()) {
                        Log.e(TAG, "Failed to load profile data", e);
                        return;
                    }
                    String name = doc.getString("name");
                    String email = doc.getString("email");
                    tvProfileName.setText(name == null || name.trim().isEmpty() ? "Student" : name);
                    tvProfileEmail.setText(email == null || email.trim().isEmpty() ? "No email set" : email);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileListener != null) profileListener.remove();
    }
}