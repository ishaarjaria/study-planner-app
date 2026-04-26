package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class ProfileActivity extends AppCompatActivity {

    LinearLayout navDashboard, navCalendar, navTasks, navProgress, navProfile;
    TextView btnBack, btnLogout, btnAccount, btnNotifications, btnEdit, btnSupport;
    Switch darkModeSwitch;

    SharedPreferences prefs;

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
        });

        // 🔹 NAVIGATION
        navDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        navCalendar.setOnClickListener(v ->
                startActivity(new Intent(this, CalendarActivity.class)));

        navTasks.setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        navProgress.setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));

        navProfile.setOnClickListener(v -> {
            // already here
        });

        // 🔹 BACK
        btnBack.setOnClickListener(v -> finish());

        // 🔹 SETTINGS BUTTONS (you can create these pages later)
        btnAccount.setOnClickListener(v ->
                startActivity(new Intent(this, AccountActivity.class)));

        btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationActivity.class)));

        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        btnSupport.setOnClickListener(v ->
                startActivity(new Intent(this, SupportActivity.class)));

        // 🔥 LOGOUT
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}