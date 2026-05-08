package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            Class<?> next = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? DashboardActivity.class
                    : LoginActivity.class;

            Intent intent = new Intent(MainActivity.this, next);
            startActivity(intent);
            finish();

        }, SPLASH_TIME);
    }
}