package com.iamlaky.emergency119.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.iamlaky.emergency119.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View pulseView = findViewById(R.id.pulseView);
        View pulseCallView = findViewById(R.id.pulseCallView);
        ConstraintLayout sosButton = findViewById(R.id.sosButton);

// Button Animation
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(pulseView, "scaleX", 1.0f, 2.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(pulseView, "scaleY", 1.0f, 2.0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(pulseView, "alpha", 0.5f, 0f);

        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        alpha.setRepeatCount(ObjectAnimator.INFINITE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(1500);
        animatorSet.start();
// Button Animation

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

        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

    }
}