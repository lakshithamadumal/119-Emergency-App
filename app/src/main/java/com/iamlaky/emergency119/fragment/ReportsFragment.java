package com.iamlaky.emergency119.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.adapter.ReportAdapter;
import com.iamlaky.emergency119.model.Report;

import java.util.ArrayList;
import java.util.List;

public class ReportsFragment extends Fragment {

    private RecyclerView rvReports;
    private ReportAdapter adapter;
    private List<Report> reportList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        rvReports = view.findViewById(R.id.rvReports);
        rvReports.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Data List
        reportList = new ArrayList<>();
        reportList.add(new Report("Car Crash", "Highlevel Rd, Maharagama", "10:30 AM", "Received", "Car Accident"));
        reportList.add(new Report("House Fire", "Kandy Town", "09:15 AM", "In Progress", "Fire"));
        reportList.add(new Report("Assault", "Nugegoda", "Yesterday", "Assigned", "Women Safety"));
        reportList.add(new Report("Heart Attack", "Colombo 03", "2 hours ago", "Completed", "Medical Emergency"));
        reportList.add(new Report("No Electricity", "Matara", "Just Now", "Received", "Power Outage"));

        adapter = new ReportAdapter(reportList);
        rvReports.setAdapter(adapter);

        return view;
    }
}