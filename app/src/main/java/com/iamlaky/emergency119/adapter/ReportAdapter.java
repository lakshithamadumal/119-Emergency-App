package com.iamlaky.emergency119.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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

    public void updateList(List<Report> newList) {
        this.reportList = newList;
        notifyDataSetChanged();
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

        holder.tvTitle.setText(report.getCategoryName());
        holder.tvLocation.setText(report.getAddress());
        holder.tvStatus.setText(report.getStatus());

        String status = report.getStatus() != null ? report.getStatus() : "Received";
        switch (status) {
            case "Received":
                holder.tvStatus.setBackgroundResource(R.drawable.report_status_blue);
                holder.tvStatus.setTextColor(Color.parseColor("#16B1FF"));
                break;
            case "Assigned":
                holder.tvStatus.setBackgroundResource(R.drawable.report_status_orange);
                holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
                break;
            case "In Progress":
                holder.tvStatus.setBackgroundResource(R.drawable.report_status_yellow);
                holder.tvStatus.setTextColor(Color.parseColor("#FFB400"));
                break;
            case "Completed":
                holder.tvStatus.setBackgroundResource(R.drawable.report_status_green);
                holder.tvStatus.setTextColor(Color.parseColor("#56CA00"));
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.report_status_blue);
                holder.tvStatus.setTextColor(Color.parseColor("#16B1FF"));
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "ID: " + report.getReportId(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() { return reportList != null ? reportList.size() : 0; }

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