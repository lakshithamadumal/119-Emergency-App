package com.iamlaky.emergency119.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.databinding.ActivityMainBinding;
import com.iamlaky.emergency119.fragment.HomeFragment;
import com.iamlaky.emergency119.fragment.ProfileFragment;
import com.iamlaky.emergency119.fragment.ReportsFragment;
import com.iamlaky.emergency119.fragment.SettingsFragment;
import com.iamlaky.emergency119.model.EmergencyReport;
import com.iamlaky.emergency119.viewmodel.UserViewModel;

import java.util.Random;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private final String EMERGENCY_NUMBER = "0718231231";

    /// Shake
    private SensorManager sensorManager;
    private float acceleration = 0f;
    private float currentAcceleration = 0f;
    private float lastAcceleration = 0f;

    private boolean isSOSProcessing = false;
    private long lastShakeTime = 0;
    /// Shake

    @Override
    protected boolean shouldCheckInternet() {
        return false;
    }

    @Override
    protected boolean shouldCheckBattery() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        if (firebaseAuth.getUid() != null) {
            userViewModel.getUser(firebaseAuth.getUid()).observe(this, user -> {
                if (user != null) {
                    if ("Expired".equals(user.getPaymentStatus())) {
                        redirectToSubscription();
                    }
                }
            });
        }

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), 0);
        }

        binding.flHome.setOnClickListener(v -> loadFragment(new HomeFragment(), 0));
        binding.flDoc.setOnClickListener(v -> loadFragment(new ReportsFragment(), 1));
        binding.flProfile.setOnClickListener(v -> loadFragment(new ProfileFragment(), 2));
        binding.flSettings.setOnClickListener(v -> loadFragment(new SettingsFragment(), 3));

        binding.btnCall.setOnClickListener(v -> {
            makePhoneCall(EMERGENCY_NUMBER);
        });

        handleIntentNavigation(getIntent());

        /// Shake Initialize
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;
        acceleration = 0.00f;
        /// Shake Initialize

        ///
        EdgeToEdge.enable(this);
    }

    private void makePhoneCall(String number) {
        if (androidx.core.content.ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {

            androidx.core.app.ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, 101);
        } else {
            performCall(number);
        }
    }

    private void performCall(String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                performCall(EMERGENCY_NUMBER);
            } else {
                Toast.makeText(this, "Permission Denied! Cannot make the call.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntentNavigation(intent);
    }

    private void handleIntentNavigation(Intent intent) {
        if (intent != null && intent.hasExtra("TARGET_FRAGMENT")) {
            String target = intent.getStringExtra("TARGET_FRAGMENT");
            if ("REPORTS".equals(target)) {
                loadFragment(new ReportsFragment(), 1);
                intent.removeExtra("TARGET_FRAGMENT");
            }
        }
    }


    public void loadFragment(Fragment fragment, int index) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

        updateNavUI(index);
    }

    private void updateNavUI(int selectedIndex) {
        binding.flHome.setBackgroundResource(R.drawable.nav_icon_bg);
        binding.flDoc.setBackgroundResource(R.drawable.nav_icon_bg);
        binding.flProfile.setBackgroundResource(R.drawable.nav_icon_bg);
        binding.flSettings.setBackgroundResource(R.drawable.nav_icon_bg);

        switch (selectedIndex) {
            case 0:
                binding.flHome.setBackgroundResource(R.drawable.home_icon_bg);
                break;
            case 1:
                binding.flDoc.setBackgroundResource(R.drawable.home_icon_bg);
                break;
            case 2:
                binding.flProfile.setBackgroundResource(R.drawable.home_icon_bg);
                break;
            case 3:
                binding.flSettings.setBackgroundResource(R.drawable.home_icon_bg);
                break;
        }
    }

    private void redirectToSubscription() {
        Toast.makeText(this, "Your subscription has expired. Please renew.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, SubscribtionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            lastAcceleration = currentAcceleration;
            currentAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = currentAcceleration - lastAcceleration;
            acceleration = acceleration * 0.9f + delta;

            if (acceleration > 12) {
                long currentTime = System.currentTimeMillis();

                if (!isSOSProcessing && (currentTime - lastShakeTime > 10000)) {

                    isSOSProcessing = true;
                    lastShakeTime = currentTime;

                    checkProfileAndProceed(() -> {
                        sendEmergencyReport();
                    });
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void checkProfileAndProceed(Runnable onSuccessAction) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phoneNumber");
                        String address = documentSnapshot.getString("address");
                        String nic = documentSnapshot.getString("nicNumber");

                        if (name == null || name.isEmpty() ||
                                phone == null || phone.isEmpty() ||
                                address == null || address.isEmpty() ||
                                nic == null || nic.isEmpty()) {

                            isSOSProcessing = false;
                            showIncompleteProfileDialog();
                        } else {
                            onSuccessAction.run();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    isSOSProcessing = false;
                    Toast.makeText(this, "Error checking profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void showIncompleteProfileDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_profile_incomplete_dialog);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.findViewById(R.id.btn_complete_profile).setOnClickListener(v -> {
            dialog.dismiss();
            loadFragment(new ProfileFragment(), 2);
        });

        dialog.findViewById(R.id.btn_not_now).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }

    private void sendEmergencyReport() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        int number = 1000 + new Random().nextInt(9000);
        String reportId = "#SOS-" + number;
        long timestamp = System.currentTimeMillis();

        EmergencyReport report = new EmergencyReport(reportId, currentUserId, timestamp);

        db.collection("emergency_reports").document(reportId).set(report)
                .addOnSuccessListener(aVoid -> {

                    addNotificationToHistory(currentUserId, "SOS Alert Sent", "SOS alert sent to 119. Help is on the way.");

                    String msg = "SOS alert sent to 119. Help is on the way.";
                    showLocalNotification("SOS Alert Sent", msg, reportId);

                    showSOSSuccessDialog(reportId);
                    isSOSProcessing = false;
                })
                .addOnFailureListener(e -> {
                    isSOSProcessing = false;
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addNotificationToHistory(String userId, String title, String description) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String notificationId = db.collection("notifications").document().getId();
        long timestamp = System.currentTimeMillis();

        com.iamlaky.emergency119.model.Notification historyNotif = new com.iamlaky.emergency119.model.Notification(
                notificationId,
                userId,
                title,
                description,
                "SOS_ALERT",
                timestamp
        );

        db.collection("notifications")
                .document(notificationId)
                .set(historyNotif)
                .addOnFailureListener(e -> Log.e("NOTIFICATION_ERROR", "Failed to save history: " + e.getMessage()));
    }

    private void showLocalNotification(String title, String message, String reportId) {
        String channelId = "emergency_alerts";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    channelId,
                    "Emergency Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for SOS and Reports");
            channel.enableLights(true);
            channel.setLightColor(android.graphics.Color.RED);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(this, R.color.mainRed))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void showSOSSuccessDialog(String reportId) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sos_sent);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }
}