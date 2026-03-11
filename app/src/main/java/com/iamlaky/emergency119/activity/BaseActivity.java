package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.iamlaky.emergency119.utils.NetworkUtil;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetworkUtil.isConnected(this)) {
            if (!this.getClass().equals(NoInternetActivity.class)) {
                Intent intent = new Intent(this, NoInternetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }
    }
}