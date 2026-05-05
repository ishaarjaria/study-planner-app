package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
<<<<<<< HEAD
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
=======
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
>>>>>>> 58c259c (new changes)

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    Button btnLogin, btnGoogle;
    TextView txtSignup;
    EditText etEmail, etPassword;

    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
<<<<<<< HEAD
=======
    GoogleSignInClient googleSignInClient;
    ActivityResultLauncher<Intent> googleSignInLauncher;
    private static final String TAG = "LoginActivity";
>>>>>>> 58c259c (new changes)

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

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() == null) {
                        Toast.makeText(this, "Google sign-in cancelled", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                                .getResult(ApiException.class);
                        if (account == null || account.getIdToken() == null) {
                            Toast.makeText(this, "Google sign-in failed: missing token", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        firebaseAuthWithGoogle(account.getIdToken(), account.getEmail());
                    } catch (ApiException e) {
                        Log.e(TAG, "Google sign-in failed: " + e.getStatusCode(), e);
                        Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // 🔥 LOGIN BUTTON
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Enter Email");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter valid email");
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
<<<<<<< HEAD
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                            fetchUserThenGoDashboard(mAuth.getCurrentUser());
=======
                            fetchUserAndOpenDashboard();
>>>>>>> 58c259c (new changes)
                        } else {
                            Exception e = task.getException();
                            logAndShowLoginError(e);
                        }
                    });
        });

        // 🔹 GOOGLE LOGIN
        btnGoogle.setOnClickListener(v -> {
            startGoogleSignIn();
        });

        // 🔹 SIGNUP (using same fields)
        txtSignup.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
<<<<<<< HEAD
=======

>>>>>>> 58c259c (new changes)
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Enter Email");
                return;
            }
<<<<<<< HEAD
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
=======
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter valid email");
                return;
            }
            if (TextUtils.isEmpty(password) || password.length() < 6) {
                etPassword.setError("Password min 6 chars");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    String firebaseMessage = e != null && e.getMessage() != null ? e.getMessage() : "Unknown error";
                    Log.e(TAG, "Signup failed: " + firebaseMessage, e);
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Email already in use. Firebase: " + firebaseMessage, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Signup failed. Firebase: " + firebaseMessage, Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(this, "Signup failed: user missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                String uid = mAuth.getCurrentUser().getUid();
                Map<String, Object> user = new HashMap<>();
                user.put("uid", uid);
                user.put("email", email);
                user.put("name", email.contains("@") ? email.substring(0, email.indexOf('@')) : "Student");
                user.put("createdAt", System.currentTimeMillis());
                firestore.collection("users").document(uid).set(user).addOnCompleteListener(saveTask -> {
                    if (!saveTask.isSuccessful()) {
                        Toast.makeText(this, "User created, profile save failed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();
                    }
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                });
            });
        });
    }

    private void fetchUserAndOpenDashboard() {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
            return;
        }
        String uid = mAuth.getCurrentUser().getUid();
        firestore.collection("users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            } else {
                ensureUserDocument(mAuth.getCurrentUser().getUid(),
                        mAuth.getCurrentUser().getEmail(),
                        mAuth.getCurrentUser().getDisplayName());
            }
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        });
    }

    private void startGoogleSignIn() {
        int webClientIdRes = getResources().getIdentifier("default_web_client_id", "string", getPackageName());
        if (webClientIdRes == 0) {
            // Manual setup required in Firebase/Google Cloud:
            // 1) In Firebase Console -> Authentication -> Sign-in method, enable Google provider.
            // 2) Add SHA-1 and SHA-256 for this app in Firebase Project settings.
            // 3) Re-download google-services.json so it includes OAuth client info.
            // 4) Sync/build app again.
            Toast.makeText(this, "Google Sign-In not configured. Enable Google provider, add SHA keys, and download updated google-services.json.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Google Sign-In not configured: default_web_client_id missing.");
            return;
        }
        String webClientId = getString(webClientIdRes);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInLauncher.launch(googleSignInClient.getSignInIntent());
    }

    private void firebaseAuthWithGoogle(String idToken, String fallbackEmail) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Exception e = task.getException();
                String firebaseMessage = e != null && e.getMessage() != null ? e.getMessage() : "Unknown error";
                Log.e(TAG, "Google Firebase auth failed: " + firebaseMessage, e);
                Toast.makeText(this, "Google auth failed. Firebase: " + firebaseMessage, Toast.LENGTH_LONG).show();
                return;
            }
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Google auth failed: user missing", Toast.LENGTH_SHORT).show();
                return;
            }
            ensureUserDocument(mAuth.getCurrentUser().getUid(),
                    mAuth.getCurrentUser().getEmail() != null ? mAuth.getCurrentUser().getEmail() : fallbackEmail,
                    mAuth.getCurrentUser().getDisplayName());
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        });
    }

    private void ensureUserDocument(String uid, String email, String displayName) {
        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid);
        user.put("email", email == null ? "" : email);
        String name = (displayName != null && !displayName.trim().isEmpty())
                ? displayName
                : ((email != null && email.contains("@")) ? email.substring(0, email.indexOf('@')) : "Student");
        user.put("name", name);
        user.put("createdAt", System.currentTimeMillis());
        firestore.collection("users").document(uid).set(user).addOnFailureListener(e ->
                Log.e(TAG, "Failed to ensure user document", e));
    }

    private void logAndShowLoginError(Exception e) {
        String firebaseMessage = e != null && e.getMessage() != null ? e.getMessage() : "Unknown error";
        Log.e(TAG, "Login failed: " + firebaseMessage, e);
        if (e instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(this, "Account not found, please sign up. Firebase: " + firebaseMessage, Toast.LENGTH_LONG).show();
            return;
        }
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            String code = ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
            if ("ERROR_WRONG_PASSWORD".equals(code) || "ERROR_INVALID_CREDENTIAL".equals(code)) {
                Toast.makeText(this, "Wrong password. Firebase: " + firebaseMessage, Toast.LENGTH_LONG).show();
                return;
            }
        }
        Toast.makeText(this, "Login failed. Firebase: " + firebaseMessage, Toast.LENGTH_LONG).show();
>>>>>>> 58c259c (new changes)
    }
}