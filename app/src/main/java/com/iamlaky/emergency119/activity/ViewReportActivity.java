package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ReportFragment;

import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.databinding.ActivityViewReportBinding;

public class ViewReportActivity extends AppCompatActivity {

    private ActivityViewReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ViewReportActivity.this, ReportFragment.class);
            intent.putExtra("TARGET_FRAGMENT", "REPORT_FRAGMENT");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        setupTimelineData();
    }

    private void setupTimelineData() {
        updateStep(binding.stepReceived.getRoot(), "Received", "11:45 AM", "Emergency request received by the system.", true);
        updateStep(binding.stepAssigned.getRoot(), "Assigned", "11:50 AM", "An officer has been assigned to your location.", true);
        updateStep(binding.stepInProgress.getRoot(), "In Progress", "12:05 PM", "Help is on the way to your location.", true);
        updateStep(binding.stepCompleted.getRoot(), "Completed", "12:30 PM", "The emergency has been successfully resolved.", false);

        binding.stepCompleted.dot.setBackgroundResource(R.drawable.timeline_dot_green);
        binding.stepReceived.dot.setBackgroundResource(R.drawable.timeline_dot_blue);
    }

    private void updateStep(View stepView, String status, String time, String desc, boolean showLine) {
        ((TextView) stepView.findViewById(R.id.tvStepStatus)).setText(status);
        ((TextView) stepView.findViewById(R.id.tvStepTime)).setText(time);
        ((TextView) stepView.findViewById(R.id.tvStepDesc)).setText(desc);

        View line = stepView.findViewById(R.id.viewLine);
        line.setVisibility(showLine ? View.VISIBLE : View.GONE);
    }
}