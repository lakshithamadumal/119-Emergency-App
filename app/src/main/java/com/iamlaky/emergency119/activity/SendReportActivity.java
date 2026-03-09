package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.adapter.CategoryAdapter;
import com.iamlaky.emergency119.databinding.ActivitySendReportBinding;
import com.iamlaky.emergency119.model.Category;

import java.util.ArrayList;
import java.util.List;

public class SendReportActivity extends AppCompatActivity {

    private ActivitySendReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySendReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupCategoryRecyclerView();

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
        binding.btnSubmit.setOnClickListener(v -> {
            Intent intent = new Intent(SendReportActivity.this, ReportSuccessActivity.class);
            startActivity(intent);
        });
    }

    private void setupCategoryRecyclerView() {
        binding.rvCategories.setLayoutManager(new GridLayoutManager(this, 3));

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Fire", R.drawable.ic_emergency_fire));
        categoryList.add(new Category("Accident", R.drawable.ic_emergency_car_accident));
        categoryList.add(new Category("Medical", R.drawable.ic_emergency_medical));
        categoryList.add(new Category("Robbery", R.drawable.ic_emergency_robbery));
        categoryList.add(new Category("Ragging", R.drawable.ic_emergency_campus_ragging));
        categoryList.add(new Category("Safety", R.drawable.ic_emergency_child_safety));
        categoryList.add(new Category("Power", R.drawable.ic_emergency_power_outage));
        categoryList.add(new Category("Travel", R.drawable.ic_emergency_travel));
        categoryList.add(new Category("Women", R.drawable.ic_emergency_women_safety));
        categoryList.add(new Category("Other", R.drawable.ic_emergency_other));

        CategoryAdapter adapter = new CategoryAdapter(categoryList);
        binding.rvCategories.setAdapter(adapter);
    }
}