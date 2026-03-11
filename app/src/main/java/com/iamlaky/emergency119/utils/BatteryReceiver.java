package com.iamlaky.emergency119.utils;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import com.iamlaky.emergency119.R;

public class BatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_LOW.equals(intent.getAction())) {
            showCustomDialog(context);
        }
    }

    private void showCustomDialog(Context context) {
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