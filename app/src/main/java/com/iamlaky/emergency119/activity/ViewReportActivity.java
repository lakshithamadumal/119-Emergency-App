package com.iamlaky.emergency119.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.databinding.ActivityViewReportBinding;
import com.iamlaky.emergency119.model.Report;
import com.iamlaky.emergency119.viewmodel.ReportViewModel;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewReportActivity extends AppCompatActivity {

    private ActivityViewReportBinding binding;
    private ReportViewModel viewModel;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        String reportId = getIntent().getStringExtra("REPORT_ID");

        if (reportId != null) {
            viewModel.listenToReport(reportId);
            observeReport();
        }

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void observeReport() {
        viewModel.selectedReport.observe(this, report -> {
            if (report != null) {
                updateUI(report);
            }
        });
    }

    private void updateUI(Report report) {
        binding.tvReportTitleDetail.setText(report.getCategoryName());
        binding.tvLocationDetail.setText(report.getAddress());
        binding.tvReferenceId.setText("Reference ID: " + report.getReportId());
        binding.tvStatusBadgeDetail.setText(report.getStatus());

        if (report.getCategoryId() != null) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("categories")
                    .document(report.getCategoryId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String imgUrl = documentSnapshot.getString("imageUrl");
                            if (imgUrl != null && !imgUrl.isEmpty()) {
                                com.bumptech.glide.Glide.with(this)
                                        .load(imgUrl)
                                        .placeholder(R.drawable.ic_placeholder)
                                        .error(R.drawable.ic_placeholder)
                                        .into(binding.ivCategoryIcon);
                            }
                        }
                    });
        }

        String receivedTime = report.getTimestamp() != null ? timeFormat.format(report.getTimestamp()) : "--:--";
        if (report.getTimestamp() != null) {
            binding.tvDateTimeDetail.setText("Today, " + receivedTime);
        }

        String currentStatus = report.getStatus() != null ? report.getStatus() : "Received";

        updateStepUI(binding.stepReceived.getRoot(), "Received", receivedTime,
                "Emergency request received by the system.", true, R.drawable.timeline_dot_blue, true);

        boolean isAssigned = isStatusActive(currentStatus, "Assigned");
        updateStepUI(binding.stepAssigned.getRoot(), "Assigned",
                (isAssigned && report.getAssignedTimestamp() != null) ? timeFormat.format(report.getAssignedTimestamp()) : "--:--",
                "An officer has been assigned to your location.", true, R.drawable.timeline_dot_yellow, isAssigned);

        boolean isInProgress = isStatusActive(currentStatus, "In Progress");
        updateStepUI(binding.stepInProgress.getRoot(), "In Progress",
                (isInProgress && report.getInProgressTimestamp() != null) ? timeFormat.format(report.getInProgressTimestamp()) : "--:--",
                "Help is on the way to your location.", true, R.drawable.timeline_dot_yellow, isInProgress);

        boolean isCompleted = isStatusActive(currentStatus, "Completed");
        updateStepUI(binding.stepCompleted.getRoot(), "Completed",
                (isCompleted && report.getCompletedTimestamp() != null) ? timeFormat.format(report.getCompletedTimestamp()) : "--:--",
                "The emergency has been successfully resolved.", false, R.drawable.timeline_dot_green, isCompleted);
    }

    private boolean isStatusActive(String current, String target) {
        if (current.equals("Completed")) return true;
        if (current.equals("In Progress") && (target.equals("In Progress") || target.equals("Assigned") || target.equals("Received"))) return true;
        if (current.equals("Assigned") && (target.equals("Assigned") || target.equals("Received"))) return true;
        return current.equals("Received") && target.equals("Received");
    }

    private void updateStepUI(View stepView, String status, String time, String desc, boolean showLine, int dotRes, boolean isActive) {
        ((TextView) stepView.findViewById(R.id.tvStepStatus)).setText(status);
        ((TextView) stepView.findViewById(R.id.tvStepTime)).setText(time);
        ((TextView) stepView.findViewById(R.id.tvStepDesc)).setText(desc);

        View dot = stepView.findViewById(R.id.dot);
        View line = stepView.findViewById(R.id.viewLine);

        line.setVisibility(showLine ? View.VISIBLE : View.GONE);

        if (isActive) {
            stepView.setAlpha(1.0f);
            dot.setBackgroundResource(dotRes);
        } else {
            stepView.setAlpha(0.3f);
            dot.setBackgroundResource(R.drawable.timeline_dot_yellow);
        }
    }
}