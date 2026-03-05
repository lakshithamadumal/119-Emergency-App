package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.iamlaky.emergency119.databinding.ActivityPaymentSuccessBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentSuccessActivity extends AppCompatActivity {

    private ActivityPaymentSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String orderId = getIntent().getStringExtra("order_id");
        double amount = getIntent().getDoubleExtra("amount", 0.0);
        String currency = getIntent().getStringExtra("currency");
        String userName = getIntent().getStringExtra("user_name");

        if (userName == null || userName.isEmpty()) {
            userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }

        if (userName == null) userName = "Emergency 119 User";

        binding.tvAmount.setText(String.format(Locale.US, "%s %,.2f", currency, amount));

        setupDetailRow(binding.rowRef, "Ref Number", orderId);
        setupDetailRow(binding.rowMerchant, "Merchant Name", "Emergency 119 App");
        setupDetailRow(binding.rowMethod, "Payment Method", "Online Payment (PayHere)");

        String currentTime = new SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.getDefault()).format(new Date());
        setupDetailRow(binding.rowTime, "Payment Time", currentTime);

        setupDetailRow(binding.rowSender, "Sender", userName);

        binding.btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupDetailRow(com.iamlaky.emergency119.databinding.RowReceiptDetailBinding rowBinding, String label, String value) {
        rowBinding.label.setText(label);
        rowBinding.value.setText(value);
    }
}