package com.iamlaky.emergency119.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.activity.ViewReportActivity;
import com.iamlaky.emergency119.model.Report;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<Report> reportList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ReportAdapter(List<Report> reportList) {
        this.reportList = reportList;
    }

    public void updateList(List<Report> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() { return reportList.size(); }
            @Override
            public int getNewListSize() { return newList.size(); }
            @Override
            public boolean areItemsTheSame(int oldP, int newP) {
                return reportList.get(oldP).getReportId().equals(newList.get(newP).getReportId());
            }
            @Override
            public boolean areContentsTheSame(int oldP, int newP) {
                return reportList.get(oldP).equals(newList.get(newP));
            }
        });
        this.reportList = newList;
        diffResult.dispatchUpdatesTo(this);
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

        if (report.getCategoryId() != null) {
            db.collection("categories").document(report.getCategoryId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String imgUrl = documentSnapshot.getString("imageUrl");
                            if (imgUrl != null && !imgUrl.isEmpty()) {
                                Glide.with(holder.itemView.getContext())
                                        .load(imgUrl)
                                        .placeholder(R.drawable.report)
                                        .error(R.drawable.report)
                                        .circleCrop() // රවුමට පෙන්වන්න
                                        .into(holder.ivIcon);
                            }
                        }
                    });
        }

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
            Intent intent = new Intent(v.getContext(), ViewReportActivity.class);
            intent.putExtra("REPORT_ID", report.getReportId());
            v.getContext().startActivity(intent);
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