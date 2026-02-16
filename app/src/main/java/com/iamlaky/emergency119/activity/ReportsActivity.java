package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.adapter.ReportAdapter;
import com.iamlaky.emergency119.model.Report;
import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {

    private RecyclerView rvReports;
    private ReportAdapter adapter;
    private List<Report> reportList;
    private FrameLayout flHome, flProfile, flSettings, btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reports);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvTitle).getRootView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvReports = findViewById(R.id.rvReports);
        rvReports.setLayoutManager(new LinearLayoutManager(this));

        reportList = new ArrayList<>();
        reportList.add(new Report("Car Crash", "Highlevel Rd, Maharagama", "10:30 AM", "Received", "Car Accident"));
        reportList.add(new Report("House Fire", "Kandy Town", "09:15 AM", "In Progress", "Fire"));
        reportList.add(new Report("Assault", "Nugegoda", "Yesterday", "Assigned", "Women Safety"));
        reportList.add(new Report("Heart Attack", "Colombo 03", "2 hours ago", "Completed", "Medical Emergency"));
        reportList.add(new Report("No Electricity", "Matara", "Just Now", "Received", "Power Outage"));

        adapter = new ReportAdapter(reportList);
        rvReports.setAdapter(adapter);

        setupNavigation();
    }

    private void setupNavigation() {
        flHome = findViewById(R.id.flHome);
        flProfile = findViewById(R.id.flProfile);
        flSettings = findViewById(R.id.flSettings);
        btnCall = findViewById(R.id.btnCall);

        btnCall.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:119"));
            startActivity(callIntent);
        });

    }
}