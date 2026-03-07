package com.iamlaky.emergency119.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.fragment.HomeFragment;
import com.iamlaky.emergency119.fragment.ProfileFragment;
import com.iamlaky.emergency119.fragment.ReportsFragment;
import com.iamlaky.emergency119.fragment.SettingsFragment;
import com.iamlaky.emergency119.viewmodel.UserViewModel;

public class MainActivity extends AppCompatActivity {

    private FrameLayout flHome, flDoc, flProfile, flSettings, btnCall;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

/// ViewModel get
UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

    userViewModel.getUser(firebaseAuth.getUid()).observe(this, user -> {
        if (user != null) {
            if ("Expired".equals(user.getPaymentStatus())) {
                
                redirectToSubscription();
            }
        }
    });

        View pulseCallView = findViewById(R.id.pulseCallView);
        // Call Button Animation
        ObjectAnimator cScaleX = ObjectAnimator.ofFloat(pulseCallView, "scaleX", 1.0f, 1.4f);
        ObjectAnimator cScaleY = ObjectAnimator.ofFloat(pulseCallView, "scaleY", 1.0f, 1.4f);
        ObjectAnimator cAlpha = ObjectAnimator.ofFloat(pulseCallView, "alpha", 1.0f, 0f);

        cScaleX.setRepeatCount(ObjectAnimator.INFINITE);
        cScaleY.setRepeatCount(ObjectAnimator.INFINITE);
        cAlpha.setRepeatCount(ObjectAnimator.INFINITE);

        AnimatorSet callPulseSet = new AnimatorSet();
        callPulseSet.playTogether(cScaleX, cScaleY, cAlpha);
        callPulseSet.setDuration(2000);
        callPulseSet.start();
        // Call Button Animation

        // Initialize Views
        flHome = findViewById(R.id.flHome);
        flDoc = findViewById(R.id.flDoc);
        flProfile = findViewById(R.id.flProfile);
        flSettings = findViewById(R.id.flSettings);
        btnCall = findViewById(R.id.btnCall);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), 0);
        }

        flHome.setOnClickListener(v -> loadFragment(new HomeFragment(), 0));
        flDoc.setOnClickListener(v -> loadFragment(new ReportsFragment(), 1));
        flProfile.setOnClickListener(v -> loadFragment(new ProfileFragment(), 2));
        flSettings.setOnClickListener(v -> loadFragment(new SettingsFragment(), 3));

        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });
    }

    private void loadFragment(Fragment fragment, int index) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        updateNavUI(index);
    }

    private void updateNavUI(int selectedIndex) {
        flHome.setBackgroundResource(R.drawable.nav_icon_bg);
        flDoc.setBackgroundResource(R.drawable.nav_icon_bg);
        flProfile.setBackgroundResource(R.drawable.nav_icon_bg);
        flSettings.setBackgroundResource(R.drawable.nav_icon_bg);

        switch (selectedIndex) {
            case 0: flHome.setBackgroundResource(R.drawable.home_icon_bg); break;
            case 1: flDoc.setBackgroundResource(R.drawable.home_icon_bg); break;
            case 2: flProfile.setBackgroundResource(R.drawable.home_icon_bg); break;
            case 3: flSettings.setBackgroundResource(R.drawable.home_icon_bg); break;
        }
    }

    private void redirectToSubscription() {
        Toast.makeText(this, "Your subscription has expired. Please renew.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(MainActivity.this, SubscribtionActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}