package com.iamlaky.emergency119.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.fragment.HomeFragment;
import com.iamlaky.emergency119.fragment.ProfileFragment;
import com.iamlaky.emergency119.fragment.ReportsFragment;
import com.iamlaky.emergency119.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private FrameLayout flHome, flDoc, flProfile, flSettings, btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        // පළමුවෙන්ම Home Fragment එක පෙන්වන්න
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), 0);
        }

        // Click Listeners
        flHome.setOnClickListener(v -> loadFragment(new HomeFragment(), 0));
        flDoc.setOnClickListener(v -> loadFragment(new ReportsFragment(), 1));
        flProfile.setOnClickListener(v -> loadFragment(new ProfileFragment(), 2));
        flSettings.setOnClickListener(v -> loadFragment(new SettingsFragment(), 3));

        // මැද SOS/Call button එක එබුවම කෙලින්ම Map Activity එකට යන්න
        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });
    }

    private void loadFragment(Fragment fragment, int index) {
        // Fragment එක මාරු කිරීම
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        // Bottom Bar එකේ Icons වල Background පාට මාරු කිරීම (Highlight)
        updateNavUI(index);
    }

    private void updateNavUI(int selectedIndex) {
        // සියලුම icons වලට සාමාන්‍ය background එක ලබා දීම
        flHome.setBackgroundResource(R.drawable.nav_icon_bg);
        flDoc.setBackgroundResource(R.drawable.nav_icon_bg);
        flProfile.setBackgroundResource(R.drawable.nav_icon_bg);
        flSettings.setBackgroundResource(R.drawable.nav_icon_bg);

        // තෝරාගත් icon එකේ background එක රතු/පාට background එකට මාරු කිරීම
        switch (selectedIndex) {
            case 0: flHome.setBackgroundResource(R.drawable.home_icon_bg); break;
            case 1: flDoc.setBackgroundResource(R.drawable.home_icon_bg); break;
            case 2: flProfile.setBackgroundResource(R.drawable.home_icon_bg); break;
            case 3: flSettings.setBackgroundResource(R.drawable.home_icon_bg); break;
        }
    }
}