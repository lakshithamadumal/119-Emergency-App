package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.databinding.ActivityViewReportBinding;
import com.iamlaky.emergency119.model.Report;
import com.iamlaky.emergency119.viewmodel.ReportViewModel;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewReportActivity extends BaseActivity {

    private ActivityViewReportBinding binding;
    private ReportViewModel viewModel;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private String operatorNumber = "";

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

        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ViewReportActivity.this, MainActivity.class);
            intent.putExtra("TARGET_FRAGMENT", "REPORTS");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
            finish();

        });

        fetchOperatorNumber();

        binding.btnCallOperator.setOnClickListener(v -> {
            if (!operatorNumber.isEmpty()) {
                makePhoneCall(operatorNumber);
            } else {
                Toast.makeText(this, "Operator number not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeReport() {
        viewModel.selectedReport.observe(this, report -> {
            if (report != null) {
                updateUI(report);
            } else {
                Toast.makeText(this, "Report no longer exists", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ViewReportActivity.this, MainActivity.class);
                intent.putExtra("TARGET_FRAGMENT", "REPORTS");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                finish();
            }
        });
    }

    private void updateUI(Report report) {
        binding.tvReportTitleDetail.setText(report.getCategoryName());
        binding.tvLocationDetail.setText(report.getAddress());
        binding.tvReferenceId.setText("Reference ID: " + report.getReportId());
        binding.tvStatusBadgeDetail.setText(report.getStatus());

        updateStatusBadge(report.getStatus());

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
///  Delete Report
        if ("Received".equals(report.getStatus())) {
            binding.btnCancel.setEnabled(true);
            binding.btnCancel.setAlpha(1.0f);
            binding.btnCancel.setOnClickListener(v -> showCancelDialog(report.getReportId()));
        } else {
            binding.btnCancel.setEnabled(false);
            binding.btnCancel.setAlpha(0.3f);
        }
///
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

    private void showCancelDialog(String reportId) {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_cancel_report);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.findViewById(R.id.btnNo).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.btnConfirmCancel).setOnClickListener(v -> {
            dialog.dismiss();
            deleteReportFromFirestore(reportId);
        });

        dialog.show();
    }

    private void deleteReportFromFirestore(String reportId) {
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("reports")
                .document(reportId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Report Deleted Successfully", Toast.LENGTH_SHORT).show();
                    decrementUserReportCount();

                    Intent intent = new Intent(ViewReportActivity.this, MainActivity.class);
                    intent.putExtra("TARGET_FRAGMENT", "REPORTS");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error deleting report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void decrementUserReportCount() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        java.util.Map<String, Object> updateData = new java.util.HashMap<>();
        updateData.put("totalReports", com.google.firebase.firestore.FieldValue.increment(-1));

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .set(updateData, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    Log.d("DB_SUCCESS", "User report count decremented");
                })
                .addOnFailureListener(e -> {
                    Log.e("DB_ERROR", "Failed to decrement count: " + e.getMessage());
                });
    }

    private void updateStatusBadge(String status) {
        if (status == null) return;

        switch (status) {
            case "Received":
                binding.tvStatusBadgeDetail.setText("Received");
                binding.tvStatusBadgeDetail.setTextColor(Color.parseColor("#16B1FF"));
                binding.tvStatusBadgeDetail.setBackgroundResource(R.drawable.report_status_blue);
                break;

            case "Assigned":
                binding.tvStatusBadgeDetail.setText(status);
                binding.tvStatusBadgeDetail.setTextColor(Color.parseColor("#FF9800"));
                binding.tvStatusBadgeDetail.setBackgroundResource(R.drawable.report_status_orange);
                break;

            case "In Progress":
                binding.tvStatusBadgeDetail.setText(status);
                binding.tvStatusBadgeDetail.setTextColor(Color.parseColor("#FFB400"));
                binding.tvStatusBadgeDetail.setBackgroundResource(R.drawable.report_status_yellow);
                break;

            case "Completed":
                binding.tvStatusBadgeDetail.setText("Completed");
                binding.tvStatusBadgeDetail.setTextColor(Color.parseColor("#56CA00"));
                binding.tvStatusBadgeDetail.setBackgroundResource(R.drawable.report_status_green);
                break;
        }
    }

    private void fetchOperatorNumber() {
        FirebaseFirestore.getInstance()
                .collection("operators")
                .document("1")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Object val = documentSnapshot.get("number");
                        operatorNumber = String.valueOf(val);
                    }
                });
    }

    private void makePhoneCall(String number) {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {

            androidx.core.app.ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE}, 102);
        } else {
            performCall(number);
        }
    }

    private void performCall(String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(android.net.Uri.parse("tel:" + number));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                if (!operatorNumber.isEmpty()) {
                    performCall(operatorNumber);
                }
            } else {
                Toast.makeText(this, "Permission Denied! Cannot call operator.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}