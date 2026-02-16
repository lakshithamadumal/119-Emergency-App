package com.iamlaky.emergency119.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.iamlaky.emergency119.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        View pulseCallView = findViewById(R.id.pulseCallView);
        Button btnMedicalInfo = findViewById(R.id.btnMedicalInfo);
        Button btnUpdate = findViewById(R.id.btnUpdate);

        setupInput(R.id.inputFullName, "Full name", "Lakshitha Madumal", InputType.TYPE_CLASS_TEXT);

        setupInput(R.id.inputPhone, "Phone Number", "+94 71 234 5678", InputType.TYPE_CLASS_PHONE);

        setupInput(R.id.inputAddress, "Address", "10th, Mile Post, Ella", InputType.TYPE_CLASS_TEXT);

        setupInput(R.id.inputNIC, "NIC / Passport Number", "200330800692", InputType.TYPE_CLASS_TEXT);

        startCallPulseAnimation(pulseCallView);

        btnMedicalInfo.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, MedicalInfoActivity.class);
            startActivity(intent);
        });

        btnUpdate.setOnClickListener(view -> {
        });
    }

    private void setupInput(int id, String label, String value, int inputType) {
        View view = findViewById(id);
        if (view != null) {
            TextView tvLabel = view.findViewById(R.id.tvInputLabel);
            EditText etValue = view.findViewById(R.id.etInputValue);

            tvLabel.setText(label);
            etValue.setText(value);
            etValue.setInputType(inputType);
        }
    }

    private void startCallPulseAnimation(View view) {
        ObjectAnimator cScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.4f);
        ObjectAnimator cScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.4f);
        ObjectAnimator cAlpha = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0f);

        cScaleX.setRepeatCount(ObjectAnimator.INFINITE);
        cScaleY.setRepeatCount(ObjectAnimator.INFINITE);
        cAlpha.setRepeatCount(ObjectAnimator.INFINITE);

        AnimatorSet callPulseSet = new AnimatorSet();
        callPulseSet.playTogether(cScaleX, cScaleY, cAlpha);
        callPulseSet.setDuration(2000);
        callPulseSet.start();
    }
}