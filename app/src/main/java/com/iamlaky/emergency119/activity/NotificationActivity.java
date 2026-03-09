package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.adapter.NotificationAdapter;
import com.iamlaky.emergency119.databinding.ActivityNotificationBinding;
import com.iamlaky.emergency119.model.NotificationModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        notificationList.add(new NotificationModel("Use moni for payment at selected merchants and get 15% discount", "11.00 AM"));
        notificationList.add(new NotificationModel("Only for you, transfer to another bank free of charge", "11.00 AM"));
        notificationList.add(new NotificationModel("Emergency alert: Heavy rain expected in your area.", "09.30 AM"));
        notificationList.add(new NotificationModel("Update: Your profile information has been successfully updated.", "Yesterday"));

        adapter = new NotificationAdapter(notificationList);
        binding.rvNotifications.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}