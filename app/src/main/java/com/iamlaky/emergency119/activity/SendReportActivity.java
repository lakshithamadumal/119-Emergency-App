package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.adapter.CategoryAdapter;
import com.iamlaky.emergency119.databinding.ActivitySendReportBinding;
import com.iamlaky.emergency119.model.Category;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;

import java.util.ArrayList;
import java.util.List;

public class SendReportActivity extends AppCompatActivity {

    private ActivitySendReportBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private CategoryAdapter adapter;
    private List<Category> categoryList;

    private double reportLat;
    private double reportLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySendReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        categoryList = new ArrayList<>();

        reportLat = getIntent().getDoubleExtra("LATITUDE", 0.0);
        reportLng = getIntent().getDoubleExtra("LONGITUDE", 0.0);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupPreviewMap();
        setupCategoryRecyclerView();

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSubmit.setOnClickListener(v -> {
            Intent intent = new Intent(SendReportActivity.this, ReportSuccessActivity.class);
            startActivity(intent);
        });
    }

    private void setupPreviewMap() {
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, style -> {
            Point point = Point.fromLngLat(reportLng, reportLat);
            binding.mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(15.0).build());

            addMarkerToMap(point);
        });
    }

    private void addMarkerToMap(Point point) {
        AnnotationPlugin annotationApi = AnnotationPluginImplKt.getAnnotations(binding.mapView);
        PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationApi, new AnnotationConfig());

        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withPoint(point).withIconImage(android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_preview_pin));

        pointAnnotationManager.create(pointAnnotationOptions);
    }

    private void setupCategoryRecyclerView() {
        binding.rvCategories.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new CategoryAdapter(categoryList);
        binding.rvCategories.setAdapter(adapter);

        firebaseFirestore.collection("categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categoryList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Category category = document.toObject(Category.class);
                    categoryList.add(category);
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.e("FIRESTORE_ERROR", "Error fetching categories", task.getException());
                Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }
}