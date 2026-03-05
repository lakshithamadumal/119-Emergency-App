package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.databinding.ActivityLoginBinding;
import com.iamlaky.emergency119.databinding.ActivitySignUpBinding;
import com.iamlaky.emergency119.model.User;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ActivitySignUpBinding binding;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setContentView(binding.getRoot());

/// Goto MainActivity(Sign Up Process)
        binding.btnSignUp.setOnClickListener(view -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (fullName.isEmpty()) {
                binding.etFullName.setError("Full name is required");
                return;
            }
            if (email.isEmpty()) {
                binding.etEmail.setError("Email is required");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.setError("Invalid email format");
                return;
            }
            if (password.isEmpty()) {
                binding.etPassword.setError("Password is required");
                return;
            }
            if (password.length() < 6) {
                binding.etPassword.setError("Password must be at least 6 characters long");
                return;
            }

            binding.btnSignUp.setEnabled(false);
            binding.btnSignUp.setAlpha(0.5f);

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = task.getResult().getUser().getUid();

                    User user = User.builder().uid(uid).name(fullName).email(email).paymentStatus("Pending").expiryDate(0).totalReports(0).profilePicUrl("").phoneNumber("").address("").nicNumber("").bloodGroup("").allergies("").medicalConditions("").currentMedications("").build();

                    firebaseFirestore.collection("users").document(uid).set(user).addOnSuccessListener(unused -> {
                        Toast.makeText(SignUpActivity.this, "Account Created! Please subscribe.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, SubscribtionActivity.class);
                        startActivity(intent);
                        finish(); //
                    }).addOnFailureListener(e -> {
                        binding.btnSignUp.setEnabled(true);
                        binding.btnSignUp.setAlpha(1.0f);
                        Toast.makeText(SignUpActivity.this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

                } else {
                    binding.btnSignUp.setEnabled(true);
                    binding.btnSignUp.setAlpha(1.0f);
                    String error = task.getException() != null ? task.getException().getMessage() : "Sign Up Failed";
                    Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });

/// Goto Login Activity
        binding.tvLoginLink.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }
}