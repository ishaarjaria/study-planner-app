package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogle;
    private TextView txtSignup;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        txtSignup = findViewById(R.id.txtSignup);

        // AUTO LOGIN
        if (mAuth.getCurrentUser() != null) {
            openDashboard();
        }

        // GOOGLE RESULT
        googleLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        GoogleSignInAccount account = GoogleSignIn
                                .getSignedInAccountFromIntent(result.getData())
                                .getResult(ApiException.class);

                        firebaseAuthWithGoogle(account.getIdToken());

                    } catch (Exception e) {
                        Log.e(TAG, "Google failed", e);
                        Toast.makeText(this, "Google login failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // LOGIN
        btnLogin.setOnClickListener(v -> loginUser());

        // SIGNUP
        txtSignup.setOnClickListener(v -> signupUser());

        // GOOGLE
        btnGoogle.setOnClickListener(v -> startGoogleLogin());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (!validate(email, pass)) return;

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(auth -> {
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                    openDashboard();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Login error", e);
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void signupUser() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (!validate(email, pass)) return;

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(auth -> {

                    String uid = mAuth.getCurrentUser().getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("uid", uid);
                    user.put("email", email);
                    user.put("name", email.split("@")[0]);

                    firestore.collection("users")
                            .document(uid)
                            .set(user);

                    Toast.makeText(this, "Signup Success", Toast.LENGTH_SHORT).show();
                    openDashboard();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Signup error", e);
                    Toast.makeText(this, "Signup failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private boolean validate(String email, String pass) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter email");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email");
            return false;
        }
        if (TextUtils.isEmpty(pass) || pass.length() < 6) {
            etPassword.setError("Min 6 chars");
            return false;
        }
        return true;
    }

    private void openDashboard() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    // 🔥 GOOGLE LOGIN
    private void startGoogleLogin() {

        String clientId = getString(
                getResources().getIdentifier("default_web_client_id", "string", getPackageName())
        );

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        )
                .requestIdToken(clientId)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleLauncher.launch(googleSignInClient.getSignInIntent());
    }

    private void firebaseAuthWithGoogle(String token) {
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);

        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(auth -> openDashboard())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Google auth error", e);
                    Toast.makeText(this, "Google login failed", Toast.LENGTH_SHORT).show();
                });
    }
}