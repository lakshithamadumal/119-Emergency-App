package com.iamlaky.emergency119.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.activity.MedicalInfoActivity;
import com.iamlaky.emergency119.databinding.FragmentProfileBinding;
import com.iamlaky.emergency119.model.User;
import com.iamlaky.emergency119.viewmodel.UserViewModel;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        String uid = firebaseAuth.getUid();
        if (uid != null) {
            userViewModel.getUser(uid).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    updateUI(user);
                }
            });
        }

        binding.btnMedicalInfo.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MedicalInfoActivity.class));
        });

        binding.btnUpdate.setOnClickListener(v -> {
            updateProfileData();
        });

        return binding.getRoot();
    }

    private void updateUI(User user) {
        binding.tvUserName.setText(user.getName() != null ? user.getName() : "Add your name");
        binding.tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "No email linked");

        if (user.getProfilePicUrl() != null && !user.getProfilePicUrl().isEmpty()) {
            Glide.with(this).load(user.getProfilePicUrl()).placeholder(R.drawable.profile_placeholder).into(binding.ivProfileImage);
        }

        setupInput(binding.inputFullName.getRoot(), "Full name", user.getName(), "Enter your full name");
        setupInput(binding.inputPhone.getRoot(), "Phone Number", user.getPhoneNumber(), "e.g. +94771234567");
        setupInput(binding.inputAddress.getRoot(), "Address", user.getAddress(), "Enter your home address");
        setupInput(binding.inputNIC.getRoot(), "NIC / Passport", user.getNicNumber(), "Enter NIC or Passport number");

        binding.etNick1.setText(user.getEmergencyNick1());
        binding.etEmergencyNum1.setText(user.getEmergencyContact1());
        binding.etNick2.setText(user.getEmergencyNick2());
        binding.etEmergencyNum2.setText(user.getEmergencyContact2());

        int count = user.getTotalReports();
        binding.tvTotalReports.setText(count > 0 ? count + "+" : "0");
    }

    private void updateProfileData() {
        String uid = firebaseAuth.getUid();
        if (uid == null) return;

        binding.btnUpdate.setEnabled(false);
        binding.btnUpdate.setText("Updating...");

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", binding.inputFullName.etInputValue.getText().toString().trim());
        updates.put("phoneNumber", binding.inputPhone.etInputValue.getText().toString().trim());
        updates.put("address", binding.inputAddress.etInputValue.getText().toString().trim());
        updates.put("nicNumber", binding.inputNIC.etInputValue.getText().toString().trim());
        updates.put("emergencyNick1", binding.etNick1.getText().toString().trim());
        updates.put("emergencyContact1", binding.etEmergencyNum1.getText().toString().trim());
        updates.put("emergencyNick2", binding.etNick2.getText().toString().trim());
        updates.put("emergencyContact2", binding.etEmergencyNum2.getText().toString().trim());

        FirebaseFirestore.getInstance().collection("users").document(uid).update(updates).addOnSuccessListener(unused -> {
            binding.btnUpdate.setEnabled(true);
            binding.btnUpdate.setText("UPDATE");

            clearFormFocus();

            Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            binding.btnUpdate.setEnabled(true);
            binding.btnUpdate.setText("UPDATE");
            Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void clearFormFocus() {
        binding.inputFullName.etInputValue.clearFocus();
        binding.inputPhone.etInputValue.clearFocus();
        binding.inputAddress.etInputValue.clearFocus();
        binding.inputNIC.etInputValue.clearFocus();
        binding.etNick1.clearFocus();
        binding.etEmergencyNum1.clearFocus();
        binding.etNick2.clearFocus();
        binding.etEmergencyNum2.clearFocus();

        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void setupInput(View inputView, String label, String value, String hint) {
        if (inputView != null) {
            TextView tvLabel = inputView.findViewById(R.id.tvInputLabel);
            EditText etValue = inputView.findViewById(R.id.etInputValue);
            if (tvLabel != null) tvLabel.setText(label);
            if (etValue != null) {
                etValue.setHint(hint);
                etValue.setText(value != null ? value : "");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}