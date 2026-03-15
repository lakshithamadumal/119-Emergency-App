package com.iamlaky.emergency119.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.iamlaky.emergency119.R;

public class NotificationPermissionActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                completeProcess();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_permission);

        findViewById(R.id.btn_allow_notifications).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                completeProcess();
            }
        });

        findViewById(R.id.btn_skip).setOnClickListener(v -> {
            completeProcess();
        });
    }

    private void completeProcess() {
        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        pref.edit().putBoolean("notif_skipped", true).apply();

        startActivity(new Intent(this, NotificationActivity.class));
        finish();
    }
}