package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iamlaky.emergency119.databinding.ActivityReportSuccessBinding;

public class ReportSuccessActivity extends AppCompatActivity {

    private ActivityReportSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding = ActivityReportSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String reportId = getIntent().getStringExtra("REPORT_ID");

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goToMainActivity();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        binding.btnTrackReport.setOnClickListener(v -> {
            if (reportId != null) {
                Intent mainIntent = new Intent(ReportSuccessActivity.this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                Intent viewIntent = new Intent(ReportSuccessActivity.this, ViewReportActivity.class);
                viewIntent.putExtra("REPORT_ID", reportId);

                startActivities(new Intent[]{mainIntent, viewIntent});

                finish();
            } else {
                Intent intent = new Intent(ReportSuccessActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(ReportSuccessActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}