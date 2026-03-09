package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnTrackReport.setOnClickListener(v -> {
            Intent intent = new Intent(ReportSuccessActivity.this, ViewReportActivity.class);
            startActivity(intent);

            finish();
        });
    }
}