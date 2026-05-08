package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SetupProfile4Activity extends AppCompatActivity {

    Button btnBack, btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile4);

        // INIT
        btnBack = findViewById(R.id.btnBack);
        btnComplete = findViewById(R.id.btnComplete);

        // BACK
        btnBack.setOnClickListener(v -> navigateToLanding());

        // COMPLETE SETUP
        btnComplete.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SetupProfile4Activity.this,
                    DashboardActivity.class
            );

            // CLEAR BACKSTACK
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        });
    }

    private void navigateToLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}