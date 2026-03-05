package com.iamlaky.emergency119.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.activity.MedicalInfoActivity;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button btnMedicalInfo = view.findViewById(R.id.btnMedicalInfo);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);

        setupInput(view, R.id.inputFullName, "Full name", "Lakshitha Madumal", InputType.TYPE_CLASS_TEXT);
        setupInput(view, R.id.inputPhone, "Phone Number", "+94 71 234 5678", InputType.TYPE_CLASS_PHONE);
        setupInput(view, R.id.inputAddress, "Address", "10th, Mile Post, Ella", InputType.TYPE_CLASS_TEXT);
        setupInput(view, R.id.inputNIC, "NIC / Passport Number", "200330800692", InputType.TYPE_CLASS_TEXT);


        btnMedicalInfo.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MedicalInfoActivity.class);
            startActivity(intent);
        });

        btnUpdate.setOnClickListener(v -> {

        });

        return view;
    }

    private void setupInput(View parentView, int id, String label, String value, int inputType) {
        View inputView = parentView.findViewById(id);
        if (inputView != null) {
            TextView tvLabel = inputView.findViewById(R.id.tvInputLabel);
            EditText etValue = inputView.findViewById(R.id.etInputValue);

            tvLabel.setText(label);
            etValue.setText(value);
            etValue.setInputType(inputType);
        }
    }

}