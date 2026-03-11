package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.iamlaky.emergency119.utils.BatteryReceiver;
import com.iamlaky.emergency119.utils.NetworkUtil;

public abstract class BaseActivity extends AppCompatActivity {

    private BatteryReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        batteryReceiver = new BatteryReceiver();
    }

    protected boolean shouldCheckInternet() {
        return true;
    }

    protected boolean shouldCheckBattery() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (shouldCheckInternet() && !NetworkUtil.isConnected(this)) {
            if (!this.getClass().equals(NoInternetActivity.class)) {
                Intent intent = new Intent(this, NoInternetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }

        if (shouldCheckBattery()) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(batteryReceiver, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (shouldCheckBattery() && batteryReceiver != null) {
            try {
                unregisterReceiver(batteryReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}