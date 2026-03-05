package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iamlaky.emergency119.fragment.HomeFragment;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.adapter.NotificationAdapter;
import com.iamlaky.emergency119.model.NotificationModel;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        rvNotifications = findViewById(R.id.rvNotifications);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });


        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        notificationList.add(new NotificationModel("Use moni for payment at selected merchants and get 15% discount", "11.00 AM"));
        notificationList.add(new NotificationModel("Only for you, transfer to another bank free of charge", "11.00 AM"));
        notificationList.add(new NotificationModel("Emergency alert: Heavy rain expected in your area.", "09.30 AM"));
        notificationList.add(new NotificationModel("Update: Your profile information has been successfully updated.", "Yesterday"));

        adapter = new NotificationAdapter(notificationList);
        rvNotifications.setAdapter(adapter);
    }
}