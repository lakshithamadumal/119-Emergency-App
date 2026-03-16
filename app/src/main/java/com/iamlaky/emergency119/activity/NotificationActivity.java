package com.iamlaky.emergency119.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iamlaky.emergency119.adapter.NotificationAdapter;
import com.iamlaky.emergency119.databinding.ActivityNotificationBinding;
import com.iamlaky.emergency119.viewmodel.NotificationViewModel;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private NotificationAdapter adapter;
    private NotificationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (shouldRequestPermission()) {
            startActivity(new Intent(this, NotificationPermissionActivity.class));
            finish();
            return;
        }

        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        setupRecyclerView();
        observeViewModel();

        binding.btnBack.setOnClickListener(view -> finish());

        EdgeToEdge.enable(this);
    }

    private boolean shouldRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return false;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return !pref.getBoolean("notif_skipped", false);
    }

    private void setupRecyclerView() {
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(new ArrayList<>());
        binding.rvNotifications.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.notifications.observe(this, notifications -> {
            if (notifications != null) {
                adapter = new NotificationAdapter(notifications);
                binding.rvNotifications.setAdapter(adapter);
            }
        });
        viewModel.fetchNotifications();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}