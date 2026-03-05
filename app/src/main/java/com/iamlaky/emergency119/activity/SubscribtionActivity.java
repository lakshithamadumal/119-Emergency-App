package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.databinding.ActivityLoginBinding;
import com.iamlaky.emergency119.databinding.ActivitySubscribtionBinding;

public class SubscribtionActivity extends AppCompatActivity {

    private ActivitySubscribtionBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private double subscriptionPrice = 0.0;
    private String currencyType = "LKR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySubscribtionBinding.inflate(getLayoutInflater());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setContentView(binding.getRoot());

        getSubscriptionDetails();

        binding.btnActivate.setOnClickListener(view -> {

        });
    }

    private void getSubscriptionDetails() {
        firebaseFirestore.collection("app_settings").document("subscription_config").get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                Double price = documentSnapshot.getDouble("price");
                String currency = documentSnapshot.getString("currency");

                if (price != null && currency != null) {
                    subscriptionPrice = price;
                    currencyType = currency;

                    String formattedPrice = String.format("%s %,.2f", currencyType, subscriptionPrice);
                    binding.subscriptionPrice.setText(formattedPrice);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading price: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
        });
    }
}