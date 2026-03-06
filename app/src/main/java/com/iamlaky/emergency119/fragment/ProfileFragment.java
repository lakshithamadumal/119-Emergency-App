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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.activity.MedicalInfoActivity;
import com.iamlaky.emergency119.databinding.FragmentProfileBinding;
import com.iamlaky.emergency119.model.User;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        fetchUserProfile();

        binding.btnMedicalInfo.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MedicalInfoActivity.class));
        });

        binding.btnUpdate.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    private void fetchUserProfile() {
        String uid = firebaseAuth.getUid();
        if (uid == null) return;

        firebaseFirestore.collection("users").document(uid).get().addOnSuccessListener(ds -> {
            if (ds.exists()) {
                User user = ds.toObject(User.class);
                if (user != null) {
                    updateUI(user);
                }
            }
        });
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

        binding.etNick1.setHint("e.g. Mom / Brother");
        if (user.getEmergencyNick1() != null) binding.etNick1.setText(user.getEmergencyNick1());
        binding.etEmergencyNum1.setHint("+94 71 234 5678");
        if (user.getEmergencyContact1() != null) binding.etEmergencyNum1.setText(user.getEmergencyContact1());

        binding.etNick2.setHint("e.g. Dad / Friend");
        if (user.getEmergencyNick2() != null) binding.etNick2.setText(user.getEmergencyNick2());
        binding.etEmergencyNum2.setHint("+94 71 888 5678");
        if (user.getEmergencyContact2() != null) binding.etEmergencyNum2.setText(user.getEmergencyContact2());

        // Reports Count
        int count = user.getTotalReports();
        if (count > 0) {
            binding.tvTotalReports.setText(count + "+");
        } else {
            binding.tvTotalReports.setText("0");
        }
    }

    private void setupInput(View inputView, String label, String value, String hint) {
        if (inputView != null) {
            TextView tvLabel = inputView.findViewById(R.id.tvInputLabel);
            EditText etValue = inputView.findViewById(R.id.etInputValue);

            if (tvLabel != null) tvLabel.setText(label);
            if (etValue != null) {
                etValue.setHint(hint);
                if (value != null && !value.isEmpty()) {
                    etValue.setText(value);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}