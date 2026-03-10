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
import com.iamlaky.emergency119.viewmodel.ReportViewModel;
import java.util.ArrayList;

public class ReportsFragment extends Fragment {

    private FragmentReportsBinding binding;
    private ReportAdapter adapter;
    private ReportViewModel reportViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        setupRecyclerView();
        observeViewModel();

        reportViewModel.fetchMyReports();
    }

    private void setupRecyclerView() {
        binding.rvReports.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ReportAdapter(new ArrayList<>());
        binding.rvReports.setAdapter(adapter);
    }

    private void observeViewModel() {
        reportViewModel.myReports.observe(getViewLifecycleOwner(), reports -> {
            if (reports != null) {
                adapter.updateList(reports);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}