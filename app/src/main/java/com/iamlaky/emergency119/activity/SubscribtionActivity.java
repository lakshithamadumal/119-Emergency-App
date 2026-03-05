package com.iamlaky.emergency119.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import java.util.HashMap;
import java.util.Map;

import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class SubscribtionActivity extends AppCompatActivity {

    private ActivitySubscribtionBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private double subscriptionPrice = 0.0;
    private String currencyType = "LKR";
    private String myOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySubscribtionBinding.inflate(getLayoutInflater());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setContentView(binding.getRoot());

        getSubscriptionDetails();

/// Payment Button
        binding.btnActivate.setOnClickListener(view -> {
            if (subscriptionPrice > 0) {
                startPayHerePayment();
            } else {
                Toast.makeText(this, "Please wait, loading price...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startPayHerePayment() {
        String uid = firebaseAuth.getUid();

        firebaseFirestore.collection("users").document(uid).get().addOnSuccessListener(ds -> {
            if (ds.exists()) {
                String name = ds.getString("name");
                String email = ds.getString("email");
                String phone = ds.getString("phoneNumber");

                InitRequest req = new InitRequest();
                req.setSandBox(true);

                req.setMerchantId("1234319");
                req.setMerchantSecret("MzE3NTUyNzIwMDMwMTk1MTE4MzA0NjE5MjQyOTQxOTM0ODg0MDIw");

                req.setCurrency(currencyType);
                req.setAmount(subscriptionPrice);
                myOrderId = "SUBS_" + System.currentTimeMillis();
                req.setOrderId(myOrderId);
                req.setItemsDescription("Emergency 119 - Annual Membership");

                req.getCustomer().setFirstName(name != null ? name : "User");
                req.getCustomer().setEmail(email != null ? email : "example@gmail.com");
                req.getCustomer().setLastName("");

                req.getCustomer().setPhone(phone != null && !phone.isEmpty() ? phone : "+94771234567");

                req.getCustomer().getAddress().setAddress("No.1, Galle Road");
                req.getCustomer().getAddress().setCity("Colombo");
                req.getCustomer().getAddress().setCountry("Sri Lanka");

                Intent intent = new Intent(this, PHMainActivity.class);
                intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);

                payhereLauncher.launch(intent);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private final ActivityResultLauncher<Intent> payhereLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Intent data = result.getData();
            if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {

                PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

                if (response != null && response.isSuccess()) {
                    updateSubscriptionStatus();
                    Log.i("PAYHERE", "Payment Success!");
                } else {
                    Log.e("PAYHERE", "Payment Failed");
                }
            }
        }
    });

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

    private void updateSubscriptionStatus() {
        String uid = firebaseAuth.getUid();
        if (uid == null) return;

        long oneYearInMillis = 31536000000L;
        long newExpiryDate = System.currentTimeMillis() + oneYearInMillis;

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("paymentStatus", "Active");
        updateData.put("expiryDate", newExpiryDate);

        firebaseFirestore.collection("users").document(uid)
                .update(updateData)
                .addOnSuccessListener(unused -> {

                    firebaseFirestore.collection("users").document(uid).get().addOnSuccessListener(ds -> {
                        String fullName = ds.getString("name");

                        Intent intent = new Intent(SubscribtionActivity.this, PaymentSuccessActivity.class);
                        intent.putExtra("order_id", myOrderId);
                        intent.putExtra("amount", subscriptionPrice);
                        intent.putExtra("currency", currencyType);
                        intent.putExtra("user_name", fullName);
                        startActivity(intent);
                        finish();
                    });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}