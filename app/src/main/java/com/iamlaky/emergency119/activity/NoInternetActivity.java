package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.utils.NetworkUtil;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        findViewById(R.id.btn_refresh).setOnClickListener(v -> {
            if (NetworkUtil.isConnected(this)) {
                Intent intent = new Intent(NoInternetActivity.this, MainActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Still no internet!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!NetworkUtil.isConnected(this)) {
            Toast.makeText(this, "Please check your connection", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}