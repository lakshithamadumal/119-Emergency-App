package com.iamlaky.emergency119.activity;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iamlaky.emergency119.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        setupUI();

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnUpdatePassword.setOnClickListener(v -> handlePasswordUpdate());
    }

    private void setupUI() {
        binding.inputCurrentPassword.tvInputLabel.setText("Current Password");
        binding.inputCurrentPassword.etInputValue.setHint("Enter current password");
        binding.inputCurrentPassword.etInputValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // New Password
        binding.inputNewPassword.tvInputLabel.setText("New Password");
        binding.inputNewPassword.etInputValue.setHint("Enter new password");
        binding.inputNewPassword.etInputValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Confirm New Password
        binding.inputConfirmPassword.tvInputLabel.setText("Confirm New Password");
        binding.inputConfirmPassword.etInputValue.setHint("Re-type new password");
        binding.inputConfirmPassword.etInputValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void handlePasswordUpdate() {
        String currentPwd = binding.inputCurrentPassword.etInputValue.getText().toString().trim();
        String newPwd = binding.inputNewPassword.etInputValue.getText().toString().trim();
        String confirmPwd = binding.inputConfirmPassword.etInputValue.getText().toString().trim();

        if (currentPwd.isEmpty()) {
            binding.inputCurrentPassword.etInputValue.setError("Current password is required");
            binding.inputCurrentPassword.etInputValue.requestFocus();
            return;
        }

        if (newPwd.isEmpty()) {
            binding.inputNewPassword.etInputValue.setError("New password is required");
            binding.inputNewPassword.etInputValue.requestFocus();
            return;
        }

        if (newPwd.length() < 6) {
            binding.inputNewPassword.etInputValue.setError("Password must be at least 6 characters");
            binding.inputNewPassword.etInputValue.requestFocus();
            return;
        }

        if (confirmPwd.isEmpty()) {
            binding.inputConfirmPassword.etInputValue.setError("Please confirm your password");
            binding.inputConfirmPassword.etInputValue.requestFocus();
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            binding.inputConfirmPassword.etInputValue.setError("Passwords do not match");
            binding.inputConfirmPassword.etInputValue.requestFocus();
            return;
        }

        updateFirebasePassword(currentPwd, newPwd);
    }

    private void updateFirebasePassword(String currentPwd, String newPwd) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {

            binding.btnUpdatePassword.setEnabled(false);
            binding.btnUpdatePassword.setText("Updating...");

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPwd);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPwd).addOnCompleteListener(updateTask -> {
                        binding.btnUpdatePassword.setEnabled(true);
                        binding.btnUpdatePassword.setText("UPDATE PASSWORD");

                        if (updateTask.isSuccessful()) {
                            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    binding.btnUpdatePassword.setEnabled(true);
                    binding.btnUpdatePassword.setText("UPDATE PASSWORD");
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}