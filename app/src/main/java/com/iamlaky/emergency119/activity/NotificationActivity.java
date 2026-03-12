package com.iamlaky.emergency119.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iamlaky.emergency119.adapter.NotificationAdapter;
import com.iamlaky.emergency119.databinding.ActivityNotificationBinding;
import com.iamlaky.emergency119.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isSkipped = pref.getBoolean("notif_skipped", false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED && !isSkipped) {

                Intent intent = new Intent(this, NotificationPermissionActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }

        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(view -> {
            finish();
        });

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();

        long currentTime = System.currentTimeMillis();

        notificationList.add(new Notification("N1", "U001", "Use moni for payment at selected merchants and get 15% discount", "PROMO", currentTime));
        notificationList.add(new Notification("N2", "U001", "Only for you, transfer to another bank free of charge", "PROMO", currentTime - 3600000));
        notificationList.add(new Notification("N3", "U001", "Emergency alert: Heavy rain expected in your area.", "ALERT", currentTime - 7200000));
        notificationList.add(new Notification("N4", "U001", "Update: Your profile information has been successfully updated.", "SYSTEM", currentTime - 86400000));

        adapter = new NotificationAdapter(notificationList);
        binding.rvNotifications.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}