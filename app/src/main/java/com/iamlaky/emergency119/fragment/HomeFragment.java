package com.iamlaky.emergency119.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.activity.MainActivity;
import com.iamlaky.emergency119.activity.MapActivity;
import com.iamlaky.emergency119.activity.NotificationActivity;
import com.iamlaky.emergency119.databinding.FragmentHomeBinding;
import com.iamlaky.emergency119.viewmodel.ReportViewModel;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ReportViewModel reportViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        reportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        reportViewModel.allReportsCount.observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                binding.tvAllReportsCount.setText("+" + count);
            }
        });

        reportViewModel.fetchAllReportsCount();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startPulseAnimation();

        binding.sosButton.setOnClickListener(v -> {
            checkProfileAndNavigateToMap();
        });

        binding.notificationContainer.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), NotificationActivity.class));
        });
    }

    private void checkProfileAndNavigateToMap() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phoneNumber");
                        String address = documentSnapshot.getString("address");
                        String nic = documentSnapshot.getString("nicNumber");

                        if (name == null || name.isEmpty() ||
                                phone == null || phone.isEmpty() ||
                                address == null || address.isEmpty() ||
                                nic == null || nic.isEmpty()) {

                            showIncompleteProfileDialog();
                        } else {
                            startActivity(new Intent(getActivity(), MapActivity.class));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showIncompleteProfileDialog() {
        if (getActivity() == null) return;

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_profile_incomplete_dialog);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.findViewById(R.id.btn_complete_profile).setOnClickListener(v -> {
            dialog.dismiss();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new ProfileFragment(), 2);
            }
        });

        dialog.findViewById(R.id.btn_not_now).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
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