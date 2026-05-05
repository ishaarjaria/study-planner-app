package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnGoogle;
    TextView txtSignup;
    EditText etEmail, etPassword;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 🔹 Firebase init
        mAuth = FirebaseAuth.getInstance();

        // 🔹 Link views
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        txtSignup = findViewById(R.id.txtSignup);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // 🔥 AUTO LOGIN
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }

        // 🔥 LOGIN BUTTON (REAL LOGIN)
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Enter Email");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Enter Password");
                return;
            }

            if (password.length() < 6) {
                etPassword.setError("Minimum 6 characters");
                return;
            }

            // Firebase login
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this,
                                    "Login Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // 🔹 GOOGLE LOGIN (for now same)
        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "Google Login Coming Soon", Toast.LENGTH_SHORT).show();
        });

        // 🔹 SIGNUP
        txtSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, LandingActivity.class));
        });
    }
}