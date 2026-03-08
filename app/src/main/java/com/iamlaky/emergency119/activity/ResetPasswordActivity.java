package com.iamlaky.emergency119.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.iamlaky.emergency119.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Back Button Logic
        binding.btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Send Button Logic
        binding.btnSendReset.setOnClickListener(v -> {
            String email = binding.etResetEmail.getText().toString().trim();

            if (email.isEmpty()) {
                binding.etResetEmail.setError("Email is required");
                return;
            }

            sendPasswordReset(email);
        });
    }

    private void sendPasswordReset(String email) {
        binding.btnSendReset.setEnabled(false);
        binding.btnSendReset.setText("Sending...");

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {

                if (!task.getResult().getSignInMethods().isEmpty()) {

                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(resetTask -> {
                        binding.btnSendReset.setEnabled(true);
                        binding.btnSendReset.setText("SEND NEW PASSWORD");

                        if (resetTask.isSuccessful()) {
                            Toast.makeText(this, "Reset link sent!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to send link: " + resetTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    binding.btnSendReset.setEnabled(true);
                    binding.btnSendReset.setText("SEND NEW PASSWORD");
                    Toast.makeText(this, "Account not found", Toast.LENGTH_LONG).show();
                }
            } else {
                binding.btnSendReset.setEnabled(true);
                binding.btnSendReset.setText("SEND NEW PASSWORD");
                String errorMsg = task.getException() != null ? task.getException().getMessage() : "An error occurred";
                Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}