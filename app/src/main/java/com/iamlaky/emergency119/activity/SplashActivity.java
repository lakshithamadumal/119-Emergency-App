package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.model.User;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        try {
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            firebaseFirestore.setFirestoreSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                checkUserSubscription(currentUser.getUid());
            } else {
                startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                finish();
            }

        }, 2500);
    }

    private void checkUserSubscription(String uid) {
        firebaseFirestore.collection("users").document(uid).get().addOnSuccessListener(ds -> {
            if (ds.exists()) {
                User user = ds.toObject(User.class);

                if (user != null) {
                    long currentTime = System.currentTimeMillis();

                    if ("Active".equals(user.getPaymentStatus()) && user.getExpiryDate() > currentTime) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(this, "Your subscription has expired!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SplashActivity.this, SubscribtionActivity.class));
                    }
                }
            } else {
                startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
            }
            finish();
        }).addOnFailureListener(e -> {
            startActivity(new Intent(SplashActivity.this, NoInternetActivity.class));
            finish();
        });
    }
}