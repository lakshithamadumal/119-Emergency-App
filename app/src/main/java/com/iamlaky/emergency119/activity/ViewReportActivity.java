package com.iamlaky.emergency119.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.iamlaky.emergency119.R;

public class ViewReportActivity extends AppCompatActivity {

    private ImageView ivCategoryIcon;
    private TextView tvTitle, tvLocation, tvStatusBadge, tvDateTime, tvRefId;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        initViews();

        btnBack.setOnClickListener(v -> finish());

        setupTimelineData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ivCategoryIcon = findViewById(R.id.ivCategoryIcon);
        tvTitle = findViewById(R.id.tvReportTitleDetail);
        tvLocation = findViewById(R.id.tvLocationDetail);
        tvStatusBadge = findViewById(R.id.tvStatusBadgeDetail);
        tvDateTime = findViewById(R.id.tvDateTimeDetail);
        tvRefId = findViewById(R.id.tvReferenceId);
    }

    private void setupTimelineData() {
        View step1 = findViewById(R.id.stepReceived);
        updateStep(step1, "Received", "11:45 AM", "Emergency request received by the system.", true);

        View step2 = findViewById(R.id.stepAssigned);
        updateStep(step2, "Assigned", "11:50 AM", "An officer has been assigned to your location.", true);

        View step3 = findViewById(R.id.stepInProgress);
        updateStep(step3, "In Progress", "12:05 PM", "Help is on the way to your location.", true);

        View step4 = findViewById(R.id.stepCompleted);
        updateStep(step4, "Completed", "12:30 PM", "The emergency has been successfully resolved.", false);

        step4.findViewById(R.id.dot).setBackgroundResource(R.drawable.timeline_dot_green);

        step1.findViewById(R.id.dot).setBackgroundResource(R.drawable.timeline_dot_blue);

    }

    private void updateStep(View stepView, String status, String time, String desc, boolean showLine) {
        ((TextView) stepView.findViewById(R.id.tvStepStatus)).setText(status);
        ((TextView) stepView.findViewById(R.id.tvStepTime)).setText(time);
        ((TextView) stepView.findViewById(R.id.tvStepDesc)).setText(desc);

        View line = stepView.findViewById(R.id.viewLine);
        line.setVisibility(showLine ? View.VISIBLE : View.GONE);
    }
}