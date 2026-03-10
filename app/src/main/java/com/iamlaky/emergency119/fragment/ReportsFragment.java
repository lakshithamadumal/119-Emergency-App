package com.iamlaky.emergency119.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.iamlaky.emergency119.adapter.ReportAdapter;
import com.iamlaky.emergency119.databinding.FragmentReportsBinding;
import com.iamlaky.emergency119.model.Report;
import com.iamlaky.emergency119.viewmodel.ReportViewModel;
import java.util.ArrayList;
import java.util.List;

public class ReportsFragment extends Fragment {

    private FragmentReportsBinding binding;
    private ReportAdapter adapter;
    private List<Report> reportList = new ArrayList<>();
    private ReportViewModel reportViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);

        reportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        setupRecyclerView();
        observeViewModel();

        reportViewModel.fetchMyReports();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.rvReports.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ReportAdapter(reportList);
        binding.rvReports.setAdapter(adapter);
    }

    private void observeViewModel() {
        reportViewModel.myReports.observe(getViewLifecycleOwner(), reports -> {
            if (reports != null) {
                reportList.clear();
                reportList.addAll(reports);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}