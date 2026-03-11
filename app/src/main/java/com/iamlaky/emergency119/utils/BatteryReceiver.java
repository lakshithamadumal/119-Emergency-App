package com.iamlaky.emergency119.utils;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.BatteryManager;
import android.view.Window;
import com.iamlaky.emergency119.R;

public class BatteryReceiver extends BroadcastReceiver {

    private static int lastAlertLevel = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);

            if ((batteryPct == 30 || batteryPct == 20 || batteryPct == 10 || batteryPct == 15) && batteryPct != lastAlertLevel) {
                lastAlertLevel = batteryPct;
                showCustomDialog(context, batteryPct);
            }
        }
    }

    private void showCustomDialog(Context context, int level) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_battery_low);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.findViewById(R.id.btn_ok).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}