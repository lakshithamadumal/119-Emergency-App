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
import androidx.fragment.app.Fragment;

import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.activity.MapActivity;
import com.iamlaky.emergency119.activity.MedicalInfoActivity;
import com.iamlaky.emergency119.activity.NotificationActivity;
import com.iamlaky.emergency119.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startPulseAnimation();

        binding.sosButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MapActivity.class));
        });

        binding.notificationContainer.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), NotificationActivity.class));
        });
    }

    private void startPulseAnimation() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(binding.pulseView, "scaleX", 1.0f, 2.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(binding.pulseView, "scaleY", 1.0f, 2.0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(binding.pulseView, "alpha", 0.5f, 0f);

        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        alpha.setRepeatCount(ObjectAnimator.INFINITE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(1500);
        animatorSet.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}