package com.iamlaky.emergency119.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iamlaky.emergency119.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Full Name Input එක හදන හැටි
        View nameInput = findViewById(R.id.inputFullName);
        ((TextView) nameInput.findViewById(R.id.tvInputLabel)).setText("Full name");
        ((EditText) nameInput.findViewById(R.id.etInputValue)).setHint("Lakshitha Madumal");

        // 2. Phone Input එක
        View phoneInput = findViewById(R.id.inputPhone);
        ((TextView) phoneInput.findViewById(R.id.tvInputLabel)).setText("Phone Number");
        ((EditText) phoneInput.findViewById(R.id.etInputValue)).setHint("+94 71 234 5678");

        // 3. Address Input එක
        View addressInput = findViewById(R.id.inputAddress);
        ((TextView) addressInput.findViewById(R.id.tvInputLabel)).setText("Address");
        ((EditText) addressInput.findViewById(R.id.etInputValue)).setHint("10th, Mile Post, Ella");

        // 4. NIC Input එක
        View nicInput = findViewById(R.id.inputNIC);
        ((TextView) nicInput.findViewById(R.id.tvInputLabel)).setText("NIC / Passport Number");
        ((EditText) nicInput.findViewById(R.id.etInputValue)).setHint("200330800692");
    }
}