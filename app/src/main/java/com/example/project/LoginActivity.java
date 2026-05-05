package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    Button btnLogin, btnGoogle;
    TextView txtSignup;
    EditText etEmail, etPassword;

    FirebaseAuth mAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 🔹 Firebase init
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // 🔹 Link views
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        txtSignup = findViewById(R.id.txtSignup);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // 🔥 AUTO LOGIN
        if (mAuth.getCurrentUser() != null) {
            fetchUserThenGoDashboard(mAuth.getCurrentUser());
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
                            fetchUserThenGoDashboard(mAuth.getCurrentUser());
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
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
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

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            FirebaseUser user = task.getResult().getUser();
                            if (user != null) {
                                saveUserProfile(user, true);
                            }
                        } else {
                            String msg = task.getException() == null ? "Signup failed" : task.getException().getMessage();
                            Toast.makeText(this, "Signup failed: " + msg, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Signup failed", task.getException());
                        }
                    });
        });
    }

    private void saveUserProfile(FirebaseUser user, boolean isNewSignup) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", user.getUid());
        userData.put("email", user.getEmail() == null ? "" : user.getEmail());
        userData.put("name", user.getDisplayName() == null ? "Student" : user.getDisplayName());
        userData.put("updatedAt", System.currentTimeMillis());
        if (isNewSignup) {
            userData.put("createdAt", System.currentTimeMillis());
        }

        firestore.collection("users").document(user.getUid())
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, isNewSignup ? "Signup Successful" : "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save user profile", e);
                    Toast.makeText(this, "User profile save failed", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                });
    }

    private void fetchUserThenGoDashboard(FirebaseUser user) {
        if (user == null) {
            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
            finish();
            return;
        }

        firestore.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        saveUserProfile(user, false);
                        return;
                    }
                    Log.d(TAG, "Fetched user document for uid: " + user.getUid());
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Fetch user failed", e);
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                });
    }
}