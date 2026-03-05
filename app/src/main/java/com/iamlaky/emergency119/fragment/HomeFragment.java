package com.iamlaky.emergency119.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.activity.MapActivity;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        View pulseView = view.findViewById(R.id.pulseView);
        ConstraintLayout sosButton = view.findViewById(R.id.sosButton);

        // --- Button Animation (Pulse Effect) ---
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

        // SOS Button Click Logic
        sosButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);

            if (getActivity() != null) {
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        return view;
    }
}