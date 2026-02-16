package com.iamlaky.emergency119.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iamlaky.emergency119.R;

public class PaymentSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_success);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View rowRef = findViewById(R.id.row_ref);
        View rowMerchant = findViewById(R.id.row_merchant);
        View rowMethod = findViewById(R.id.row_method);
        View rowTime = findViewById(R.id.row_time);
        View rowSender = findViewById(R.id.row_sender);

        ((TextView) rowRef.findViewById(R.id.label)).setText("Ref Number");
        ((TextView) rowRef.findViewById(R.id.value)).setText("#SLI-88954210");

        ((TextView) rowMerchant.findViewById(R.id.label)).setText("Merchant Name");
        ((TextView) rowMerchant.findViewById(R.id.value)).setText("SafeLife Insurance");

        ((TextView) rowMethod.findViewById(R.id.label)).setText("Payment Method");
        ((TextView) rowMethod.findViewById(R.id.value)).setText("Debit Card");

        ((TextView) rowTime.findViewById(R.id.label)).setText("Payment Time");
        ((TextView) rowTime.findViewById(R.id.value)).setText("Feb 16, 2026, 16:38");

        ((TextView) rowSender.findViewById(R.id.label)).setText("Sender");
        ((TextView) rowSender.findViewById(R.id.value)).setText("Lakshitha Madumal");
    }
}