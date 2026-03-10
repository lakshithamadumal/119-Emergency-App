package com.iamlaky.emergency119.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.databinding.ActivityMainBinding; // Binding class එක
import com.iamlaky.emergency119.fragment.HomeFragment;
import com.iamlaky.emergency119.fragment.ProfileFragment;
import com.iamlaky.emergency119.fragment.ReportsFragment;
import com.iamlaky.emergency119.fragment.SettingsFragment;
import com.iamlaky.emergency119.viewmodel.UserViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding; // Binding Object එක
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        if (firebaseAuth.getUid() != null) {
            userViewModel.getUser(firebaseAuth.getUid()).observe(this, user -> {
                if (user != null) {
                    if ("Expired".equals(user.getPaymentStatus())) {
                        redirectToSubscription();
                    }
                }
            });
        }

        setupCallAnimation();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), 0);
        }

        binding.flHome.setOnClickListener(v -> loadFragment(new HomeFragment(), 0));
        binding.flDoc.setOnClickListener(v -> loadFragment(new ReportsFragment(), 1));
        binding.flProfile.setOnClickListener(v -> loadFragment(new ProfileFragment(), 2));
        binding.flSettings.setOnClickListener(v -> loadFragment(new SettingsFragment(), 3));

        binding.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });

        handleIntentNavigation(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntentNavigation(intent);
    }

    private void handleIntentNavigation(Intent intent) {
        if (intent != null && intent.hasExtra("TARGET_FRAGMENT")) {
            String target = intent.getStringExtra("TARGET_FRAGMENT");
            if ("REPORTS".equals(target)) {
                loadFragment(new ReportsFragment(), 1);
                intent.removeExtra("TARGET_FRAGMENT");
            }
        }
    }

    private void setupCallAnimation() {
        ObjectAnimator cScaleX = ObjectAnimator.ofFloat(binding.pulseCallView, "scaleX", 1.0f, 1.4f);
        ObjectAnimator cScaleY = ObjectAnimator.ofFloat(binding.pulseCallView, "scaleY", 1.0f, 1.4f);
        ObjectAnimator cAlpha = ObjectAnimator.ofFloat(binding.pulseCallView, "alpha", 1.0f, 0f);

        cScaleX.setRepeatCount(ObjectAnimator.INFINITE);
        cScaleY.setRepeatCount(ObjectAnimator.INFINITE);
        cAlpha.setRepeatCount(ObjectAnimator.INFINITE);

        AnimatorSet callPulseSet = new AnimatorSet();
        callPulseSet.playTogether(cScaleX, cScaleY, cAlpha);
        callPulseSet.setDuration(2000);
        callPulseSet.start();
    }

    private void loadFragment(Fragment fragment, int index) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        updateNavUI(index);
    }

    private void updateNavUI(int selectedIndex) {
        binding.flHome.setBackgroundResource(R.drawable.nav_icon_bg);
        binding.flDoc.setBackgroundResource(R.drawable.nav_icon_bg);
        binding.flProfile.setBackgroundResource(R.drawable.nav_icon_bg);
        binding.flSettings.setBackgroundResource(R.drawable.nav_icon_bg);

        switch (selectedIndex) {
            case 0: binding.flHome.setBackgroundResource(R.drawable.home_icon_bg); break;
            case 1: binding.flDoc.setBackgroundResource(R.drawable.home_icon_bg); break;
            case 2: binding.flProfile.setBackgroundResource(R.drawable.home_icon_bg); break;
            case 3: binding.flSettings.setBackgroundResource(R.drawable.home_icon_bg); break;
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