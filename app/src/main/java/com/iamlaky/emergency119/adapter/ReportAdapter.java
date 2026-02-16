package com.iamlaky.emergency119.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.model.Report;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<Report> reportList;

    public ReportAdapter(List<Report> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);

        holder.tvTitle.setText(report.getTitle());
        holder.tvLocation.setText(report.getLocation());
        holder.tvStatus.setText(report.getStatus());

        switch (report.getStatus()) {
            case "Received":
                holder.tvStatus.setBackgroundResource(R.drawable.report_status_blue);
                holder.tvStatus.setTextColor(Color.WHITE);
                break;
            case "Assigned":
            case "In Progress":
                holder.tvStatus.setBackgroundResource(R.drawable.report_status_yellow);
                holder.tvStatus.setTextColor(Color.BLACK);
                break;
            case "Completed":
                holder.tvStatus.setBackgroundResource(R.drawable.report_status_green);
                holder.tvStatus.setTextColor(Color.WHITE);
                break;
            default:
                holder.tvStatus.setBackgroundColor(Color.LTGRAY);
                holder.tvStatus.setTextColor(Color.BLACK);
                break;
        }

        int iconRes;
        switch (report.getCategory()) {
            case "Car Accident":
                iconRes = R.drawable.ic_emergency_car_accident;
                break;
            case "Fire":
                iconRes = R.drawable.ic_emergency_fire;
                break;
            case "Medical Emergency":
                iconRes = R.drawable.ic_emergency_medical;
                break;
            case "Women Safety":
                iconRes = R.drawable.ic_emergency_women_safety;
                break;
            case "Power Outage":
                iconRes = R.drawable.ic_emergency_power_outage;
                break;
            default:
                iconRes = R.drawable.ic_emergency_other;
                break;
        }
        holder.ivIcon.setImageResource(iconRes);
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvLocation, tvStatus;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivReportIcon);
            tvTitle = itemView.findViewById(R.id.tvReportTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStatus = itemView.findViewById(R.id.tvStatusBadge);
        }
    }
}