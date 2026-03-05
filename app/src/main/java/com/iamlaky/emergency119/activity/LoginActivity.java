package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamlaky.emergency119.databinding.ActivityLoginBinding;
import com.iamlaky.emergency119.fragment.HomeFragment;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.model.User;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setContentView(binding.getRoot());

///  Goto MainActivity(Login Process)
        binding.btnLogin.setOnClickListener(view -> {
            String email = binding.etLoginEmail.getText().toString().trim();
            String password = binding.etLoginPassword.getText().toString().trim();

            if (email.isEmpty()) {
                binding.etLoginEmail.setError("Email is required");
                return;
            }
            if (password.isEmpty()) {
                binding.etLoginPassword.setError("Password is required");
                return;
            }

            binding.btnLogin.setEnabled(false);
            binding.btnLogin.setAlpha(0.5f);

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = firebaseAuth.getCurrentUser().getUid();

                    firebaseFirestore.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);

                            if (user != null) {
                                long currentTime = System.currentTimeMillis();

                                if ("Active".equals(user.getPaymentStatus()) && user.getExpiryDate() > currentTime) {
                                    // Payment OK -> MainActivity
                                    Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Subscription expired or inactive.", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoginActivity.this, SubscribtionActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }).addOnFailureListener(e -> {
                        binding.btnLogin.setEnabled(true);
                        binding.btnLogin.setAlpha(1.0f);
                        Toast.makeText(LoginActivity.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
                    });

                } else {
                    binding.btnLogin.setEnabled(true);
                    binding.btnLogin.setAlpha(1.0f);
                    Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                }
            });
        });

/// Goto SignUp Activity
        binding.tvSignUpLink.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

///  Goto Forgot Password Activity
        binding.tvForgotPass.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}