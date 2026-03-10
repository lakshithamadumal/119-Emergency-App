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

        // 1. SendReportActivity එකෙන් එවපු Report ID එක මෙතනදී ලබාගන්නවා
        String reportId = getIntent().getStringExtra("REPORT_ID");

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnTrackReport.setOnClickListener(v -> {
            // 2. ViewReportActivity එකට යන ගමන් ID එකත් අරන් යනවා
            if (reportId != null) {
                Intent intent = new Intent(ReportSuccessActivity.this, ViewReportActivity.class);
                intent.putExtra("REPORT_ID", reportId);
                startActivity(intent);

                // 3. Animation එකක් එක්කම ඉවර කරමු
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                // ආරක්ෂාවට: මොකක් හරි හේතුවකින් ID එක නැති වුණොත් ආපහු Main එකට යවනවා
                finish();
            }
        });


    }
}