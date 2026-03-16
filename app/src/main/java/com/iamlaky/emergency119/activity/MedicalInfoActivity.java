package com.iamlaky.emergency119.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.databinding.ActivityMedicalInfoBinding;
import com.iamlaky.emergency119.viewmodel.UserViewModel;

import java.util.HashMap;
import java.util.Map;

public class MedicalInfoActivity extends AppCompatActivity {

    private ActivityMedicalInfoBinding binding;
    private UserViewModel userViewModel;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        uid = FirebaseAuth.getInstance().getUid();

        setupUI();
        loadMedicalData();

        binding.btnUpdateMedical.setOnClickListener(v -> updateMedicalInfo());

        EdgeToEdge.enable(this);
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());

        View bloodInput = binding.inputBloodGroup.getRoot();
        ((TextView) bloodInput.findViewById(R.id.tvInputLabel)).setText("Blood Group");
        ((EditText) bloodInput.findViewById(R.id.etInputValue)).setHint("e.g. B+");
    }

    private void loadMedicalData() {
        if (uid != null) {
            userViewModel.getUser(uid).observe(this, user -> {
                if (user != null) {
                    View bloodInput = binding.inputBloodGroup.getRoot();
                    ((EditText) bloodInput.findViewById(R.id.etInputValue)).setText(user.getBloodGroup());

                    binding.etAllergies.setText(user.getAllergies());
                    binding.etConditions.setText(user.getMedicalConditions());
                    binding.etMeds.setText(user.getCurrentMedications());
                }
            });
        }
    }

    private void updateMedicalInfo() {
        String bloodGroup = ((EditText) binding.inputBloodGroup.getRoot().findViewById(R.id.etInputValue)).getText().toString().trim();
        String allergies = binding.etAllergies.getText().toString().trim();
        String conditions = binding.etConditions.getText().toString().trim();
        String meds = binding.etMeds.getText().toString().trim();

        binding.btnUpdateMedical.setEnabled(false);
        binding.btnUpdateMedical.setText("Updating...");

        Map<String, Object> medicalUpdates = new HashMap<>();
        medicalUpdates.put("bloodGroup", bloodGroup);
        medicalUpdates.put("allergies", allergies);
        medicalUpdates.put("medicalConditions", conditions);
        medicalUpdates.put("currentMedications", meds);

        FirebaseFirestore.getInstance().collection("users").document(uid).update(medicalUpdates).addOnSuccessListener(aVoid -> {
            binding.btnUpdateMedical.setEnabled(true);
            binding.btnUpdateMedical.setText("UPDATE");
            Toast.makeText(this, "Medical info updated!", Toast.LENGTH_SHORT).show();

            binding.etAllergies.clearFocus();
            binding.etConditions.clearFocus();
            binding.etMeds.clearFocus();
            binding.inputBloodGroup.getRoot().findViewById(R.id.etInputValue).clearFocus();

            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }).addOnFailureListener(e -> {
            binding.btnUpdateMedical.setEnabled(true);
            binding.btnUpdateMedical.setText("UPDATE");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}